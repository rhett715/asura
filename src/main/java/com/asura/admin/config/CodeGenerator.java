package com.asura.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @Author Rhett
 * @Date 2021/7/30
 * @Description Mybatis-Plus代码生成器
 */
public class CodeGenerator {
    /**
     * 读取控制台内容
     * @param tip 用户输入
     * @return String
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        //创建代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("Rhett");
        //是否自动生成之后打开资源管理器
        gc.setOpen(false);
        //再次生产时是否覆盖文件
        gc.setFileOverride(false);
        //生成默认生成时IService，变成Service
        gc.setServiceName("%sService");
        //主键策略
        gc.setIdType(IdType.AUTO);
        //设置data的类型，只是用java.util.data代替
        gc.setDateType(DateType.ONLY_DATE);
        //实体属性 swagger2注解
        gc.setSwagger2(true);
        autoGenerator.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/doubi?useUnicode=true&useSSL=false&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("root312");
        //使用MySQL数据库
        dsc.setDbType(DbType.MYSQL);
        autoGenerator.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        //pc.setModuleName(scanner("模块名"));
        pc.setParent("com.asura.admin");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setMapper("mapper");
        pc.setEntity("entity");
        autoGenerator.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        cfg.setFileOutConfigList(focList);
        autoGenerator.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        // 指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        autoGenerator.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        //数据库表映射到实体的命名策略：驼峰命名
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //数据库字段映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
        // 使用lombok模型
        strategy.setEntityLombokModel(true);
        // 生成restful api风格的@RestController控制器
        //strategy.setRestControllerStyle(true);
        // 公共父类
        //strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段
        //strategy.setSuperEntityColumns("id");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        //驼峰转连接符
        strategy.setControllerMappingHyphenStyle(true);
        //表前缀
        //strategy.setTablePrefix("sys_");
        //逻辑删除字段名
        //strategy.setLogicDeleteFieldName("is_deleted");
        //去掉布尔值is_前缀
        strategy.setEntityBooleanColumnRemoveIsPrefix(true);
        autoGenerator.setStrategy(strategy);
        autoGenerator.setTemplateEngine(new FreemarkerTemplateEngine());
        //执行
        autoGenerator.execute();
    }
    //gen_table,gen_table_column,sys_config,sys_dept,sys_dict_data,sys_dict_type,sys_job,sys_job_log,sys_login_info,sys_menu,sys_notice,sys_operation_log,sys_post,sys_role,sys_role_dept,sys_role_menu,sys_user,sys_user_post,sys_user_role
}
