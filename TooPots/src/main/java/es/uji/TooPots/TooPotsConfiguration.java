package es.uji.TooPots;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;

import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.dao.CustomerDao;
import es.uji.TooPots.dao.InstructorDao;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
	
	@Bean
	public Formatter<LocalDateTime> localDateFormatterISO() {
	   return new Formatter<LocalDateTime>() {
		@Override
		public String print(LocalDateTime object, Locale locale) {
			// TODO Auto-generated method stub
	           return DateTimeFormatter.ofPattern("dd-MM-yyyy").format(object); 
		}

		@Override
		public LocalDateTime parse(String text, Locale locale) throws ParseException {
			// TODO Auto-generated method stub
	           return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}
	   };
	}
	
	@Bean
	public CustomerDao customerD() {
		return new CustomerDao();
	}

}
