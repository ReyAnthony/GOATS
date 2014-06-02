/*
 	InfosTicket.java is part of :
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
package fr.anthonyrey.activities;

import org.xmlrpc.android.XMLRPCException;

import fr.anthonyrey.connector.GlpiConnector;
import fr.anthonyrey.connector.Tickets;
import fr.anthonyrey.error.FatalError;
import fr.glpi.mobile.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class is an android Activity,
 * it shows the details of the ticket you selected
 * 
 * @author anthonyrey
 *
 */
public class InfosTicket extends Activity {
	
	
	private GlpiConnector glpi;
	private Tickets tk;
	
	private Button buttonCloture;
	
	private String errorStr;
	private ProgressDialog pDialog;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        
        
        // On récupère les infos de login dans l'intent
        final String infosLogin[] = getIntent().getStringArrayExtra("infosLogin");
               
        // Et on teste la présence des infos
        if (infosLogin == null){ // si il n'y en a pas 
        	        	
        	Intent intent = new Intent(InfosTicket.this, Login.class);
			startActivity(intent); 
			finish();
			
			// On ferme l'activitée et on retourne sur login
			
        }else{
        	
        	pDialog = ProgressDialog.show(InfosTicket.this, getResources().getString(R.string.loading), getResources().getString(R.string.wait), true, false);
        	 Thread t = new Thread(new Runnable(){
     			
     			public void run(){
     			
	     			try{	
	     				
		     			// Si elles existent, alors on les récupère et on crée un nouveau connecteur GLPI
		 				glpi = new GlpiConnector(infosLogin[0],infosLogin[1],infosLogin[2],infosLogin[3]);
		 	    		// création d'un string  idTicket dans lequel on recupere la place dans la liste du ticket recuperé precedemment 
		 	    		// (faire un test de la taille du tableau aussi, pour eviter maj au moment de modifier)
		 	    		String idTicket = getIntent().getStringExtra("clickedTicket");
		 	    		glpi.addTicketsToList(idTicket);
		 	    		
		 	    		runOnUiThread(new Runnable(){
		 	    			
		 	    			public void run(){
		 	    				
		 	    				// referencement du texView a partir de celui que l'on avait crÃ©e  
				        		TextView infoNumTicket = (TextView) findViewById(R.id.infoNumTicket);
				        		
				        		//int id = Integer.valueOf(idTicket);
				        		// On reference le ticket qu'on veut dans l'arraylist
				        		// en l'occurence on en a mis qu'un seul, celui qu'on nous a renvoyé dans listing
				        		tk = glpi.getTickets(0);

				        		infoNumTicket.setText(infoNumTicket.getText()+Integer.toString(tk.getId()));
				        		
				        		TextView nomTicket = (TextView) findViewById(R.id.nomTicket);
				        		nomTicket.setText(getResources().getString(R.string.nom_du_ticket)+"\n"+tk.getNom());
				        		
				        		TextView demandeurTicket = (TextView) findViewById(R.id.demandeurTicket);
				        		demandeurTicket.setText(getResources().getString(R.string.demandeur)+"\n"+tk.getNomDemandeur());
				        		
				        		TextView dateCreaTicket = (TextView) findViewById(R.id.dateCreaTicket);
				        		dateCreaTicket.setText(getResources().getString(R.string.date_crea)+"\n"+tk.getDateCrea());

				        		TextView dateModTicket = (TextView) findViewById(R.id.dateModTicket);
				        		dateModTicket.setText(getResources().getString(R.string.date_mod)+"\n"+tk.getDateDerniereMod());
				        		
				        		TextView descTicket = (TextView) findViewById(R.id.descTicket);
				        		descTicket.setText(getResources().getString(R.string.description)+"\n"+tk.getDesc());
				        		
				        		buttonCloture = (Button) findViewById(R.id.boutonCloture);

				        		 // On crée un listener pour le bouton
				      	      	buttonCloture.setOnClickListener( 
				              		new OnClickListener() { 
				              			
				      					@Override
				      					public void onClick(View v) {
				      						
				      						buttonCloture.setClickable(false);
				      						
				      						Intent intent = new Intent(InfosTicket.this, Cloture.class);
				      						intent.putExtra("infosLogin", getIntent().getStringArrayExtra("infosLogin"));
				      						intent.putExtra("clickedTicket", getIntent().getStringExtra("clickedTicket")); 
				      						startActivity(intent);
				      						finish();
				      						
				      					}
				              		});  	
		 	    			}
		 	    			
		 	    		});
		 	    		
		 	    		pDialog.dismiss();
		 	    	
	     			}
	     			catch(XMLRPCException e){
	     				
	     				errorStr = e.toString();
	     				
	     				//pour pouvoir afficher l'erreur
						runOnUiThread(new Runnable(){
							
							public void run(){
								
								new FatalError(errorStr, InfosTicket.this);
								
	
							}
							
						});
	     				
	     				
	     			}
	     			
	     			
     			}
 	  
     			
             });
        	 t.start();
        }
    }
}
