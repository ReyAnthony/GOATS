/*
 	Login.java is part of :
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
import fr.anthonyrey.error.NonFatalError;
import fr.glpi.mobile.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * This class is an android Activity,
 * it is the class being reached each time the application starts and 
 * it also puts in place the storage in order to keep sessions related data between intents.
 * Do not even think to go to another page without going through this one once.
 * 
 * @author anthonyrey
 *
 */
public class Login extends Activity{
	
	private GlpiConnector glpi;
	private EditText champIp;
	private EditText champLogin;
	private EditText champPwd;
	
	private CheckBox credentialBox;
	private Button buttonSubmit;
	private ProgressDialog pDialog;
	private String errorStr = null;
	
	private SharedPreferences.Editor prefsEditor;
	public static final String SAVE_CREDENTIALS = "UserCredentials";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.activity_login);

		  //Here we are initialising buttons and fields
		  champIp = (EditText) findViewById(R.id.ipBox);
		  champLogin = (EditText) findViewById(R.id.logBox);
		  champPwd = (EditText) findViewById(R.id.pwdBox);
		  buttonSubmit = (Button) findViewById(R.id.button1); 
		  
		  // We init the file to save user infos
		  SharedPreferences credentials = this.getSharedPreferences(SAVE_CREDENTIALS, MODE_PRIVATE);
		  prefsEditor = credentials.edit();
		  credentialBox = (CheckBox) findViewById(R.id.credentialBox);
		  
		  // if the 2 fields already exists in pref file
		  if(credentials.contains("ip") && credentials.contains("login") ){
			  
			  // We update login and IP
			  champIp.setText(credentials.getString("ip", null));
			  champLogin.setText(credentials.getString("login", null));
			  // Then we check the checkbox
			  credentialBox.setChecked(true);
 
		  }else{
			  
			  // Otherwize we uncheck it
			  credentialBox.setChecked(false); 
		  }
		  
		  // Listenner for the button
	      buttonSubmit.setOnClickListener( 
    		new OnClickListener() { 
    			
				@Override
				public void onClick(View v) {
					
					pDialog = ProgressDialog.show(Login.this, getResources().getString(R.string.loading), getResources().getString(R.string.wait), true, false);
					// Can't click twice
					buttonSubmit.setClickable(false);
					// When clicked we are creating a new GLPI Ctor with the value we gave to the program
					glpi = new GlpiConnector(champIp.getText().toString(),champLogin.getText().toString() ,champPwd.getText().toString());
					//TODO GlpiConnector.resetNbTickets();
					
					Thread t = new Thread(new Runnable(){
						
						public void run(){
							
								try {
								
									glpi.setSessionAccordingToInfos();
									
									// We put the infos in an array
									String infosLogin[] = {champIp.getText().toString(),champLogin.getText().toString(),champPwd.getText().toString(), glpi.getSession()};
							
									if(credentialBox.isChecked()){
										
										//We fill the config file
										//Remember that the connexion must work to get here (look at the try catch)
										//We can't put anything in the file
										prefsEditor.putString("ip", champIp.getText().toString());
										prefsEditor.putString("login", champLogin.getText().toString());
										
									}else{
										
										// Otherwize we wipe it
										prefsEditor.clear(); 
									}
									
									// Anyway we are commiting the results
									prefsEditor.commit();
									
									
									// Background service gets initialized 
									Intent backgroundService = new Intent(Login.this, BackgroundService.class);
									backgroundService.putExtra("infosLogin", infosLogin);
									startService(backgroundService);
									
									
									Intent intent = new Intent(Login.this, Listing.class);
									// We give the infos in an intent
									intent.putExtra("infosLogin", infosLogin);
									startActivity(intent); 
									
									//This activity get closed
									finish();
							
								}
								catch(XMLRPCException e){
								
									errorStr = e.toString();
									pDialog.dismiss();
									buttonSubmit.setClickable(true);

									runOnUiThread(new Runnable(){
										
										public void run(){
											
											new NonFatalError(errorStr, Login.this);
											
											
										}
										
									});
								  
								
								}
								
						}
							
					});
					t.start();
				}
    	});
	      
	}
}
