package es.uji.TooPots;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.dao.InstructorDao;

import javax.sql.DataSource;

@Configuration
public class TooPotsConfiguration {
	
	@Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
	
	@Bean
	public InstructorDao instructorD() {
		return new InstructorDao();
	}
	
	@Bean
	public ActivityDao activityD() {
		return new ActivityDao();
	}
	
	@Bean
	public ActivityTypeDao activityTypeD() {
		return new ActivityTypeDao();
	}
}
