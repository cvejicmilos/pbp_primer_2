package hibernate;

import java.io.Serializable;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class SeminarskiRadPK implements Serializable {
	private Integer indeks;
	private Short skGodina;
	private Integer idPredmeta;
	
	public SeminarskiRadPK() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idPredmeta == null) ? 0 : idPredmeta.hashCode());
		result = prime * result + ((indeks == null) ? 0 : indeks.hashCode());
		result = prime * result + ((skGodina == null) ? 0 : skGodina.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeminarskiRadPK other = (SeminarskiRadPK) obj;
		if (idPredmeta == null) {
			if (other.idPredmeta != null)
				return false;
		} else if (!idPredmeta.equals(other.idPredmeta))
			return false;
		if (indeks == null) {
			if (other.indeks != null)
				return false;
		} else if (!indeks.equals(other.indeks))
			return false;
		if (skGodina == null) {
			if (other.skGodina != null)
				return false;
		} else if (!skGodina.equals(other.skGodina))
			return false;
		return true;
	}
}
