package hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name="ISPIT", schema="DA")
public class Ispit {
	@Id
	private IspitPK kljuc;
	
	@Column(nullable=false)
	private String status;
	
	@Column
	private Date datPolaganja;
	
	@Column
	private Short poeni;
	
	@Column
	private Short ocena;
	
	@ManyToOne()
	@MapsId("seminarskiRad")
	@JoinColumns({
		@JoinColumn(name="INDEKS", referencedColumnName="INDEKS"),
		@JoinColumn(name="IDPREDMETA", referencedColumnName="IDPREDMETA"),
		@JoinColumn(name="SKGODINA", referencedColumnName="SKGODINA"),
	})
	private SeminarskiRad seminarskiRad;
	
	@ManyToOne()
	@MapsId("seminarskiRad.indeks")
	@JoinColumn(name="INDEKS", referencedColumnName="INDEKS")
	private Student student;

	public void setKljuc(IspitPK kljuc) {
		this.kljuc = kljuc;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDatPolaganja(Date datPolaganja) {
		this.datPolaganja = datPolaganja;
	}

	public void setPoeni(Short poeni) {
		this.poeni = poeni;
	}

	public void setOcena(Short ocena) {
		this.ocena = ocena;
	}

	public void setSeminarskiRad(SeminarskiRad seminarskiRad) {
		this.seminarskiRad = seminarskiRad;
	}
}
