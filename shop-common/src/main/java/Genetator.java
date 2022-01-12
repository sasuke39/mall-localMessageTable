import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author xianhong
 * @date 2021/12/28
 */
public class Genetator {


    /**
     * @author xianhong
     * @date 2021/12/27
     */

        public static void main(String[] args) {
            // 代码生成器
            AutoGenerator autoGenerator = new AutoGenerator();
            // 全局配置
            GlobalConfig globalConfig = new GlobalConfig();
            //生成文件的输出目录
            String projectPath = System.getProperty("user.dir");
            String subPath = "/shop-dao";
            globalConfig.setOutputDir(projectPath+subPath + "/src/main/java");

            // Author设置作者
            globalConfig.setAuthor("xianhong.zhou");
            // 文件覆盖
            globalConfig.setFileOverride(true);
            // 生成后打开文件
            globalConfig.setOpen(false);
            // 自定义文件名风格，%s自动填充表实体属性
            globalConfig.setMapperName("%sMapper");
//            globalConfig.setXmlName("%sMapper");
//            globalConfig.setServiceName("%sDao");
//            globalConfig.setServiceImplName("%sDaoImpl");
//            globalConfig.setEntityName("%s");
//            globalConfig.setControllerName("%sController");
            autoGenerator.setGlobalConfig(globalConfig);

            // 数据源配置
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            dataSourceConfig.setDbType(DbType.MYSQL);
            dataSourceConfig.setTypeConvert(new MySqlTypeConvert());
            dataSourceConfig.setUrl("jdbc:mysql://localhost:3306/rockmqtest?tinyInt1isBit=false");
            dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
            dataSourceConfig.setUsername("root");
            dataSourceConfig.setPassword("123123..");
            autoGenerator.setDataSource(dataSourceConfig);

            // 包名配置
            PackageConfig packageConfig = new PackageConfig();
            // 父包和子包名分开处理
            packageConfig.setParent("com.myshop");
//            packageConfig.setController("web");
//            packageConfig.setEntity("pojo");
            packageConfig.setMapper("dao");
//            packageConfig.setService("service");
//            packageConfig.setServiceImpl("service.impl");
            autoGenerator.setPackageInfo(packageConfig);

            // 生成策略配置
            StrategyConfig strategy = new StrategyConfig();

            //todo 修改六：生成的表名，多个用逗号分隔
            strategy.setInclude("tz_goods,tz_goods_log,tz_balance_log,tz_coupon,tz_mq_consumer,tz_mq_produce,tz_order,tz_pay,tz_user".split(","));
            strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
            strategy.setTablePrefix(packageConfig.getModuleName() + "_"); //生成实体时去掉表前缀

            strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
            strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
            strategy.setRestControllerStyle(true); //restful api风格控制器

            autoGenerator.setStrategy(strategy);
            // 执行，以上相关参数可以基于动态输入获取
            autoGenerator.execute();
        }


}
