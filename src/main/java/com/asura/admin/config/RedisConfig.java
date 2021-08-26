package com.asura.admin.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @Author Rhett
 * @Date 2021/8/9
 * @Description redis配置
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<Object> serializer = valueSerializer();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        serializer.setObjectMapper(mapper);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(keySerializer());
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(keySerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory connectionFactory) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                //过期时间，默认1天
                .entryTtl(Duration.ofDays(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                .disableCachingNullValues();
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(configuration).transactionAware().build();
    }

    @Bean
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(limitScriptText());
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * 使用StringRedisSerializer来序列化和反序列化redis的key值
     * @return RedisSerializer<String>
     */
    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
     * @return Jackson2JsonRedisSerializer<Object>
     */
    private Jackson2JsonRedisSerializer<Object> valueSerializer() {
        return new Jackson2JsonRedisSerializer<>(Object.class);
    }

    /**
     * 限流脚本
     */
    private String limitScriptText() {
        return StrUtil.builder()
                .append("local key = KEYS[1]\n")
                .append("local count = tonumber(ARGV[1])\n")
                .append("local time = tonumber(ARGV[2])\n")
                .append("local current = redis.call('get', key);\n")
                .append("if current and tonumber(current) > count then\n")
                .append("    return current;\n")
                .append("end\n")
                .append("current = redis.call('incr', key)\n")
                .append("if tonumber(current) == 1 then\n")
                .append("    redis.call('expire', key, time)\n")
                .append("end\n")
                .append("return current;")
                .toString();
    }
}
