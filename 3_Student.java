package hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="DOSIJE", schema="DA")
public class Student {
	@Id
	private Integer indeks;
	
	@Column(name="IME", nullable=false)
	private String ime;

	@Column(name="PREZIME", nullable=false)
	private String prezime;
	
	@OneToMany(mappedBy="student")
	List<Ispit> ispiti = new ArrayList<>();

	public Integer getIndeks() {
		return indeks;
	}

	public String getIme() {
		return ime;
	}

	public String getPrezime() {
		return prezime;
	}
}
