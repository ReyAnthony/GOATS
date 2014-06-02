/*
 	Listing.java is part of :
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

import fr.anthonyrey.connector.*;
import fr.anthonyrey.error.FatalError;
import fr.glpi.mobile.R;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * This class is an android Activity,
 * here you can select a ticket from the list
 * 
 * @author anthonyrey
 *
 */
public class Listing extends ListActivity{

	private GlpiConnector glpi;
	private ListView listView;
	private int nbTickets;
	private String errorStr;
	private ProgressDialog pDialog;
	private Thread t;

	private String ticketsToShow = null;  
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // On récupère les infos de login dans l'intent
        final String infosLogin[] = getIntent().getStringArrayExtra("infosLogin");
               
        // Et on teste la présence de ces infos
        if (infosLogin == null){ // si il n'y en Ã  pas 
        	        	
        	Intent intent = new Intent(Listing.this, Login.class);
			startActivity(intent); 
			finish();
			
			// On ferme l'activitée et on retourne sur login
			
        }
        else{ 
        	
        	ticketsToShow = getIntent().getStringExtra("typeTk");
        	pDialog = ProgressDialog.show(Listing.this, getResources().getString(R.string.loading), getResources().getString(R.string.wait), true, false);
        	
			t = new Thread(new Runnable(){
				
				public void run(){
					
						try {
							
			        		glpi = new GlpiConnector(infosLogin[0],infosLogin[1],infosLogin[2],infosLogin[3]);
			        		
			        		if(ticketsToShow == null){
			        			glpi.addTicketsToList();
			        			
			        		}else if(ticketsToShow.contains("ClotureTk")){
			        			
			        			glpi.addClosedTicketsToList();
			        		}
			        		
			    			nbTickets =  GlpiConnector.getNbTickets();
			    			
			    			//pour pouvoir afficher l'erreur
							runOnUiThread(new Runnable(){
								
								public void run(){
									
									
									// On crée une liste pour l'arrayadapter
					    			String liste[] = new String[glpi.getTickets().size()]; 
					    			// On crÃ©e une liste pour stocker les id
					    			final String listeId[] = new String[glpi.getTickets().size()]; 
					    			
					    			
					    			// On y met les infos voulues sur les tickets
					    			for(int i = 0; i < glpi.getTickets().size(); i++){ 
					    				
					    				String str = getResources().getString(R.string.id)+" "+glpi.getTickets(i).getId()+"\n"+getResources().getString(R.string.nom_du_ticket)+" "+glpi.getTickets(i).getNom()+"\n"+getResources().getString(R.string.demandeur)+" "+glpi.getTickets(i).getNomDemandeur()+"\n"+getResources().getString(R.string.date_mod)+" "+glpi.getTickets(i).getDateDerniereMod();
					    				 
					    					liste[i] = str;
					    					listeId[i] = Integer.toString(glpi.getTickets().get(i).getId());
					    		
					    			}
					    			
									// On crée un adapteur
									setListAdapter(new ArrayAdapter<String>(Listing.this, android.R.layout.simple_list_item_1 , liste)); 
									
									listView = getListView();
									listView.setTextFilterEnabled(true);
									
									if(ticketsToShow == null){
										
										// Puis on lui met un listener 
										listView.setOnItemClickListener(new OnItemClickListener() {
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								
												// Qui va renvoyer vers la page Ticket
											    Intent intent = new Intent(Listing.this, InfosTicket.class); 
											    // Tout en lui passant les infos voulues
												intent.putExtra("infosLogin", getIntent().getStringArrayExtra("infosLogin"));
												// On passe l'id GLPI du ticket Ã  recuperer
												intent.putExtra("clickedTicket", listeId[position]);								 
												startActivity(intent); 
	
											}
											
											
								
										});
									}
									pDialog.dismiss();
									// On teste si y'a des tickets
									testTicket();
									
								}
								
							});
			    			
						
						
						}
						catch(XMLRPCException e){
						
							errorStr = e.toString();
							
							//pour pouvoir afficher l'erreur
							runOnUiThread(new Runnable(){
								
								public void run(){
									
									new FatalError(errorStr, Listing.this);
									
										
								}
								
							});
						  
						
						}

	 
				}
					
			});
			t.start();

	    	
    	}
		
    }
    
    
    public void testTicket(){
    	
		if(nbTickets == 0){
			  				
			
			  AlertDialog.Builder builder1 = new AlertDialog.Builder(Listing.this);
			  // On lui donne un titre
			  builder1.setTitle(getResources().getString(R.string.app_message));
			  
			 builder1.setMessage(getResources().getString(R.string.no_tickets));
		  
	          builder1.setCancelable(false);
	          builder1.setPositiveButton("Ok",
	                  new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int id) {
	            	  // Si on appuie sur OK, ça ferme
	                  dialog.dismiss();
	              }
	          });
	       
	          AlertDialog alert11 = builder1.create();
              alert11.show(); // on l'affiche
			
		}
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.listing, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        	
    	   Intent intent;
    	   
    		switch(item.getItemId()){
    			
    		case R.id.disconn:
    			
    		    startActivity(new Intent(Listing.this, Login.class));
    		    finish();
    		    //tue le processus (et donc le background intent)
    		    android.os.Process.killProcess(android.os.Process.myPid());
    			break;
    		
    		case R.id.my_tickets:
    			
    			// On la recharge
			    intent = new Intent(Listing.this, Listing.class); 
				intent.putExtra("infosLogin", getIntent().getStringArrayExtra("infosLogin"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);	
				startActivity(intent);
    			finish();
    			break;
    			
    		case R.id.closed_tickets:
    			
    			// On la recharge
			    intent = new Intent(Listing.this, Listing.class); 
				intent.putExtra("infosLogin", getIntent().getStringArrayExtra("infosLogin"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);	
				intent.putExtra("typeTk", "ClotureTk");
				startActivity(intent);
    			finish();
    			break;
    		}
    	    	
              
                return true;
        
    }
    
    @Override
    public void onBackPressed() {
    // Pas d'action

    return;
    }

    
    
}
