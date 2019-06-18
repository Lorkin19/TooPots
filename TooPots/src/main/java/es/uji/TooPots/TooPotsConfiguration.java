package es.uji.TooPots;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.Formatter;

import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.dao.CanOrganizeDao;
import es.uji.TooPots.dao.CertificateDao;
import es.uji.TooPots.dao.CustomerDao;
import es.uji.TooPots.dao.InstructorDao;
import es.uji.TooPots.dao.MessageDao;
import es.uji.TooPots.dao.ReceiveInformationDao;
import es.uji.TooPots.dao.RequestDao;
import es.uji.TooPots.dao.ReservationDao;
import es.uji.TooPots.dao.UserDao;
import es.uji.TooPots.model.ActivityType;
import es.uji.TooPots.dao.FakeUserProvider;

import java.text.ParseException;
import java.time.LocalDate;
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
	public ReservationDao reservationD() {
		return new ReservationDao();
	}
	
	@Bean
	public Formatter<LocalDate> localDateFormatterISO() {
	   return new Formatter<LocalDate>() {
		@Override
		public String print(LocalDate object, Locale locale) {
			// TODO Auto-generated method stub
	           return DateTimeFormatter.ofPattern("dd-MM-yyyy").format(object); 
		}

		@Override
		public LocalDate parse(String text, Locale locale) throws ParseException {
			// TODO Auto-generated method stub
	           return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
		}
	   };
	}
	
	@Bean
	public CustomerDao customerD() {
		return new CustomerDao();
	}
	
	@Bean
	public RequestDao requestD() {
		return new RequestDao();
	}
	
    @Bean
    @Primary
    public UserDao userD() {
    	return new FakeUserProvider();
    }
    
    @Bean
    public MessageDao messageD() {
    	return new MessageDao();
    }
    
    @Bean
    public CertificateDao certificateD() {
    	return new CertificateDao();
    }
    
    @Bean
    public ReceiveInformationDao receiveInformationD() {
    	return new ReceiveInformationDao();
    }
    
    @Bean
    public CanOrganizeDao canOrganizeD() {
    	return new CanOrganizeDao();
    }
    
    @Bean
    public ActivityType activityType() {
    	return new ActivityType();
    }
}
