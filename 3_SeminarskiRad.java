package hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="SEMINARSKI_RADOVI", schema="DA")
public class SeminarskiRad {
	@Id
	private SeminarskiRadPK kljuc;
	
	@Column(nullable=false)
	private String naslov;
	
	@Column
	private char odobren;
	
	@Column
	private Short ocena;
	
	@OneToMany(mappedBy="seminarskiRad")
	private List<Ispit> ispiti = new ArrayList<>();
	
	@ManyToOne
	@MapsId("indeks")
	@JoinColumn(name="INDEKS", referencedColumnName="INDEKS")
	private Student student;
	
	public SeminarskiRadPK getKljuc() {
		return this.kljuc;
	}

	public void setOcena(Short ocena) {
		this.ocena = ocena;
	}
	
	@Override
	public String toString() {
		return String.format("- Student: %d %s %s\n- Naslov: %s\n", this.student.getIndeks(), this.student.getIme(), this.student.getPrezime(), this.naslov);
	}
}
