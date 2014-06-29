/*
 	Cloture.java is part of :
	GOATS, Greatly Open Android Ticket Service
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

*/
package fr.anthonyrey.activities;


import org.xmlrpc.android.XMLRPCException;

import fr.anthonyrey.connector.GlpiConnector;
import fr.anthonyrey.connector.Tickets;
import fr.anthonyrey.error.FatalError;
import fr.anthonyrey.error.NonFatalError;
import fr.glpi.mobile.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * This class is an android Activity,
 * it allows you to close a ticket and eventually give a solution to it
 * 
 * @author anthonyrey
 *
 */
public class Cloture extends Activity implements OnClickListener {
	
	// Cette page ne se met pas à jour (sinon on perd ce qui à été saisi), c'est idiot.
	private GlpiConnector glpi;
	private String idTicket;
	private Tickets tk;
	private EditText solutionText;
	private Button buttonSubmit;
	protected String errorStr;
	private TextView infoNumTicket;
	private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloture);
        
        // On récupère les infos de login dans l'intent
        final String infosLogin[] = getIntent().getStringArrayExtra("infosLogin");
               
        // Et on teste la prÃ©sence des infos
        if (infosLogin == null){ // si il n'y en a pas 
        	        	
        	Intent intent = new Intent(Cloture.this, Login.class);
			startActivity(intent); 
			finish();
			
			// On ferme l'activitée et on retourne sur login
			
        }
        
        pDialog = ProgressDialog.show(Cloture.this, getResources().getString(R.string.loading), getResources().getString(R.string.wait), true, false);
    		
		Thread t = new Thread(new Runnable(){
			
			public void run(){
				
					try {
					 	
				    	// Si elles existent, alors on les récupère et on crée un nouveau connecteur GLPI
				    	glpi = new GlpiConnector(infosLogin[0],infosLogin[1],infosLogin[2],infosLogin[3]);
				    	idTicket = getIntent().getStringExtra("clickedTicket");
						glpi.addTicketsToList(idTicket);	 	
				    	
						
						// referencement du texView a partir de celui que l'on avait crée  
						infoNumTicket = (TextView) findViewById(R.id.infoNumTicket);
						buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
						buttonSubmit.setOnClickListener(Cloture.this);
						solutionText = (EditText) findViewById(R.id.solutionText);
						
						// On reference le ticket qu'on veut dans l'arraylist
						tk = glpi.getTickets(0);					
						
				      
						runOnUiThread(new Runnable(){
							
							public void run(){
									
								 // Parcontre, on associe bien l'id du ticket en question à la chaine , pas sa place dans la liste
								  infoNumTicket.setText(infoNumTicket.getText()+Integer.toString(tk.getId()));
									
							}
							
						});
						
						pDialog.dismiss();
						
						
					}
					catch(XMLRPCException e){
						

	     				errorStr = e.toString();
	     				
	     				//pour pouvoir afficher l'erreur
						runOnUiThread(new Runnable(){
							
							public void run(){
								
								new FatalError(errorStr, Cloture.this);
							
	
							}
							
						});
						
					}
						
				}
		});
		t.start();
    }

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
			
			case R.id.buttonSubmit:
				
				buttonSubmit.setClickable(false);
				
				
				Thread t = new Thread(new Runnable(){
					
					public void run(){
						
							try {
								
								if(!solutionText.getText().toString().isEmpty()){
									
									glpi.solveTicket(idTicket, solutionText.getText().toString());
									
									runOnUiThread(new Runnable(){
										
										public void run(){
											
											Toast.makeText(Cloture.this, getResources().getString(R.string.ticket_closed), Toast.LENGTH_LONG).show();
										}
										
									});	
									
		  							Intent intent = new Intent(Cloture.this, Listing.class);
		  							intent.putExtra("infosLogin", getIntent().getStringArrayExtra("infosLogin"));
		  							// Ca permet de reinitailiser l'histoirique atteignable par back et donc d'empehcer de retourner sur la page
		  							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					      			startActivity(intent); 
					      			finish();
					      			
								}else{
									
									runOnUiThread(new Runnable(){
										
										public void run(){
											
											new NonFatalError(getResources().getString(R.string.solution_please), Cloture.this);	
											buttonSubmit.setClickable(true);
										}
										
									});							
								}
								
							}catch(XMLRPCException e){
								
								errorStr = e.toString();
								//pour pouvoir afficher l'erreur
								runOnUiThread(new Runnable(){
									
									public void run(){
										
										new FatalError(errorStr, Cloture.this);
									
			
									}
									
								});
								
								
							}
							
						}
				});
				t.start();
				
				break;			
		}
	}
}

    
 
