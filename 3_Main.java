package hibernate;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

class Main {
	
	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction TR = null;
        
        try (Scanner ulaz = new Scanner(System.in);) {
    		TR = session.beginTransaction();
    		
    		System.out.println("Unesite identifikator predmeta: ");
    		Integer idPredmeta = ulaz.nextInt();
            
    		System.out.println("Unesite skolsku godinu:");
    		Short skGodina = ulaz.nextShort();
    		String oznakaRoka = "ODBRANE_SR_" + skGodina;
    		
            String hql = "FROM SeminarskiRad sr WHERE sr.odobren = 'D' AND sr.kljuc.skGodina = :skGodina AND sr.kljuc.idPredmeta = :idPredmeta";
            Query<SeminarskiRad> upit = session.createQuery(hql, SeminarskiRad.class);
            upit.setParameter("skGodina", skGodina);
            upit.setParameter("idPredmeta", idPredmeta);
            
            List<SeminarskiRad> seminarskiRadovi = upit.list();
            for(SeminarskiRad sr : seminarskiRadovi) {
            	// Ispisivanje informacija o seminarskom radu
            	System.out.println("\nSeminarski rad:");
            	System.out.println(sr);
            	
            	System.out.println("Ocenite rad: ");
            	Short ocena = ulaz.nextShort();
            	
            	// Belezenje ocene za seminarski rad
            	sr.setOcena(ocena);
            	session.update(sr);
            	
            	// Unosenje polozenog ispita
            	IspitPK kljuc = new IspitPK(sr.getKljuc(), oznakaRoka);
            	Ispit noviIspit = new Ispit();
            	noviIspit.setKljuc(kljuc);
            	noviIspit.setStatus("o");
            	noviIspit.setDatPolaganja(new Date());
            	noviIspit.setOcena(ocena);
            	noviIspit.setPoeni((short)(ocena * 10 - 5));
            	noviIspit.setSeminarskiRad(sr);
            	session.save(noviIspit);
            	
            	System.out.println("Uspesno ste ocenili seminarski rad!");
            }

            TR.commit();
        } catch (Exception e) {
            System.err.println("Ponistavanje transakcije!");
            e.printStackTrace();
            
            if (TR != null) {
                TR.rollback();
            }
        } finally {
            session.close();
        }
        
		HibernateUtil.getSessionFactory().close();
	}

}
