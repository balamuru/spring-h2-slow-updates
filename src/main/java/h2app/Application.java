package h2app;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {
    private static Logger log = Logger.getLogger(Application.class);

    public static void main(String args[]) {
        final ApplicationContext ctx = SpringApplication.run(Application.class, args);
        JdbcTemplate jdbcTemplate = ctx.getBean(JdbcTemplate.class);

        createTable(jdbcTemplate);

        insert(jdbcTemplate, 100000);

        update(jdbcTemplate);
       // retrieve(jdbcTemplate);
    }


    @Bean
    public JdbcTemplate jdbcTemplate() {
        final DataSource dataSource = dbcpDataSource();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }


    private static void update(JdbcTemplate jdbcTemplate) {
        long t1 = System.currentTimeMillis();
        System.out.println("Updating rows");
        final int updated = jdbcTemplate.update("UPDATE CUSTOMERS SET FIRST_NAME = ? WHERE VALID = ?", "VIP", true);
        long t2 = System.currentTimeMillis();
        System.out.println("Time taken for the update operation: " + (t2 - t1) / 1000 + " seconds for " + updated + " rows");
    }

    private static void createTable(JdbcTemplate jdbcTemplate) {
        System.out.println("Creating tables");
        jdbcTemplate.execute("DROP TABLE CUSTOMERS IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE CUSTOMERS(ID SERIAL, FIRST_NAME VARCHAR(255), LAST_NAME VARCHAR(255), VALID BOOLEAN)");

        System.out.println("Creating indices");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS IDX_VALID on CUSTOMERS (VALID)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS IDX_FIRST_NAME on CUSTOMERS (FIRST_NAME)");
    }

    private static void retrieve(JdbcTemplate jdbcTemplate) {
        System.out.println("Printing customer records");
        jdbcTemplate.query(
                "select * from customers ",
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        final Customer customer = new Customer(rs.getLong("ID"), rs.getString("FIRST_NAME"),
                                rs.getString("LAST_NAME"), rs.getBoolean("VALID"));
                        System.out.println("Retrieved customer record: " + customer);
                    }
                }
        );

    }

    private static void insert(JdbcTemplate jdbcTemplate, int numInserts) {
        System.out.println("Creating customer records");
        for (int i = 1; i <= numInserts; i++) {
            jdbcTemplate.update(
                    "INSERT INTO CUSTOMERS(FIRST_NAME,LAST_NAME, VALID) VALUES(?,?,?)",
                    "fName_" + i, "lName_" + i, true);
        }
    }

    private static String jdbcUrl() {
        final File dataDir = new File(String.format("%s/%s", System.getProperty("java.io.tmpdir"), "db_dir"));
        final String jdbcUrl = String.format("jdbc:h2:%s/%s", dataDir.getAbsolutePath(), "test_db");
        log.info("The chosen jdbc url is " + jdbcUrl);
        return jdbcUrl;
    }


    private DataSource dbcpDataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(jdbcUrl());
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setPoolPreparedStatements(true);
        return dataSource;
    }
}