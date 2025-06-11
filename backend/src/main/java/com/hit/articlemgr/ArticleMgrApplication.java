package com.hit.articlemgr;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// 使用MyBatis-Plus的MapperScan
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


//import com.baomidou.mybatisplus.annotation.MapperScan;
// 使用这个导入
//import com.baomidou.mybatisplus.core.annotation.MapperScan;
//import com.baomidou.mybatisplus.annotation.MapperScan;
// 或者使用 MyBatis 原生的
// import org.mybatis.spring.annotation.MapperScan;
/**
 * 文章管理器主应用程序
 *
 * @author HIT
 * @version 1.0
 */
@SpringBootApplication
@MapperScan("com.hit.articlemgr.mapper")
public class ArticleMgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleMgrApplication.class, args);
        System.out.println("🚀 文章管理系统启动成功！");
        System.out.println("📝 访问地址: http://localhost:8080/api");
        System.out.println("📚 API文档: http://localhost:8080/api/doc.html");
    }
}