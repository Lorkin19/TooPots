package es.uji.TooPots.dao;

import java.util.Collection;

import es.uji.TooPots.model.UserDetails;

public interface UserDao {
	UserDetails loadUserByMail(String mail, String password);
	Collection<UserDetails> listAllUsers();
}
