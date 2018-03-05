package com.avant.auth;

import com.avant.util.UserUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@NoArgsConstructor
public class User implements Cloneable {
	@Id
	private String id;

	private String mail, pass, name, photo, phone;

	public User(String mail, String pass) {
		this.id = UUID.randomUUID().toString();
		this.mail = mail;
		this.pass = pass;
	}
	
	public void setPass (String pass) {
		if (pass == null) {
			this.pass = null;
			return;
		}
		if (pass.length() < 32) {
			this.pass = UserUtils.encryptPass(mail, pass);
		} else {
			this.pass = pass;
		}
	}
	
	public User wipePrivateData () {
		try {
			User clone = (User) this.clone();
			clone.setPass(null);
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
