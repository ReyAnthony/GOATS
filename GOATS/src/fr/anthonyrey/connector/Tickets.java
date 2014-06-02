/*
 	Tickets.java is part of :
	GOATS, GLPI Open Android Ticket Service
    Copyright (C) 2014  Anthony REY

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    How to contact the author : 
    
    By email at : 
    anthony.rey5@gmail.com
    
    By paper mail at :
    Anthony Rey, 2 rue jean moulin, 95270, Viarmes, France
*/

package fr.anthonyrey.connector;

import java.util.Locale;


/**
 * This class defines the tickets and what are their properties
 * 
 * @author anthonyrey
 *
 */
public class Tickets {
	
	private int id;
	private String nom, desc, dateCrea, dateDerniereMod, nomDemandeur, etat;

	/**
	 * This is the constructor to make a ticket.
	 * You should not have to use it, as the tickets are already created by other methods located in GlpiConnector
	 * They are usually directly found in the GLPI DB
	 * 
	 * @param id
	 * Id of the ticket
	 * 
	 * @param nom
	 * Name of the ticket
	 * 
	 * @param desc
	 * Description of the problem
	 * 
	 * @param dateCrea
	 * Date where the ticket was created
	 * 
	 * @param dateDerniereMod
	 * Last time it was modified
	 * 
	 * @param nomDemandeur
	 * Name of the person who made the ticket
	 * 
	 * @param etat
	 * Status of the ticket
	 */
	public Tickets( int id, String nom, String desc, String dateCrea, String dateDerniereMod, String nomDemandeur, String etat)
	{
		
		this.id = id;
		this.nom = nom;
		this.desc = desc;
		
		if(Locale.getDefault().getISO3Language() == "fr"){
		
			//FR Time
			String annee = dateCrea.substring(0,4);
			String mois = dateCrea.substring(5,7);
			String jour  = dateCrea.substring(8,10);
			String heure = dateCrea.substring(11);
			
			this.dateCrea = jour+"/"+mois+"/"+annee+" "+heure  ; 
		
			annee = dateDerniereMod.substring(0,4);
			mois = dateDerniereMod.substring(5,7);
			jour  = dateDerniereMod.substring(8,10);
			heure = dateDerniereMod.substring(11);
			
			this.dateDerniereMod = jour+"/"+mois+"/"+annee+" "+heure  ; 
		
		}else{
			
			this.dateCrea = dateCrea;
			this.dateDerniereMod = dateDerniereMod;
			
		}
		
		this.nomDemandeur = nomDemandeur;
		this.etat = etat;
		
	}

	/** 
	 * @return
	 * The name of the ticket
	 */
	public String getNom() {
		return nom;
	}

	/** 
	 * @return
	 * The ID of the ticket
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return
	 * Description of the problem
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * 
	 * @return
	 * Date where the ticket was created
	 */
	public String getDateCrea() {
		return dateCrea;
	}
	
	/**
	 * 
	 * @return
	 * Last time the ticket was modified
	 */	
	public String getDateDerniereMod() {
		return dateDerniereMod;
	}

	/**
	 * 
	 * @return
	 * Whom asked for this ticket
	 */
	public String getNomDemandeur() {
		return nomDemandeur;
	}
	
	/**
	 * 
	 * @return
	 * Status of the ticket
	 */
	public String getEtat() {
		return etat;
	}
	
}
