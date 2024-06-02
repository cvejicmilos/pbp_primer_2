package hibernate;

import java.io.Serializable;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class IspitPK implements Serializable {
	private SeminarskiRadPK seminarskiRad;
	private String oznakaRoka;
	
	public IspitPK() {
	}

	public IspitPK(SeminarskiRadPK seminarskiRad, String oznakaRoka) {
		super();
		this.seminarskiRad = seminarskiRad;
		this.oznakaRoka = oznakaRoka;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((oznakaRoka == null) ? 0 : oznakaRoka.hashCode());
		result = prime * result + ((seminarskiRad == null) ? 0 : seminarskiRad.hashCode());
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
		IspitPK other = (IspitPK) obj;
		if (oznakaRoka == null) {
			if (other.oznakaRoka != null)
				return false;
		} else if (!oznakaRoka.equals(other.oznakaRoka))
			return false;
		if (seminarskiRad == null) {
			if (other.seminarskiRad != null)
				return false;
		} else if (!seminarskiRad.equals(other.seminarskiRad))
			return false;
		return true;
	}
}
