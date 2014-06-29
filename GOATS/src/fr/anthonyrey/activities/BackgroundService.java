/*
 	BackgroundService.java is part of :
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
import fr.glpi.mobile.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;


import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;


/** 
 * 
 * This class is to be only instantiated with > 16 api 
 * It creates an intentService which handle the notifications about tickets
 * 
 * @author anthonyrey
 *
 */
public class BackgroundService extends IntentService {

	
	GlpiConnector glpi;
	int previousNbTickets;
	int currentNbTickets;
	
	public BackgroundService(){
		super("Notifications");
		
	}
	

	@Override
	protected void onHandleIntent(Intent intent) {
		
		int nbNotif = 0;
		
		String infosLogin[] = intent.getStringArrayExtra("infosLogin");
	
		glpi = new GlpiConnector(infosLogin[0],infosLogin[1],infosLogin[2],infosLogin[3]);
		
		try {
			glpi.addTicketsToList();
		} catch (XMLRPCException e1) {
			    
			e1.printStackTrace();
		}
		previousNbTickets = GlpiConnector.getNbTickets();
		// On stocke le nombre de tickets
		
		while(true){
			
			// On efface la liste des tickets 
			glpi.dumpList();
			try {
				// On récupère la liste des tickets pour l'agent
				glpi.addTicketsToList();
				currentNbTickets = GlpiConnector.getNbTickets();
			} catch (XMLRPCException e) {
				
				e.printStackTrace();
			}
			
			
			
			if (previousNbTickets < currentNbTickets ){ // si le nombre de ticket ne correspond pas
			
				
			 previousNbTickets = currentNbTickets;
				 
		       Intent intentBis = new Intent(BackgroundService.this, Listing.class);
			   intentBis.putExtra("infosLogin", intent.getStringArrayExtra("infosLogin"));
			   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		       PendingIntent pIntent = PendingIntent.getActivity(BackgroundService.this, 0, intentBis, 0);
		        
		       long[] pattern = {500,500,500,500,500,500,500,500,500};
		       
		        Notification mNotification = new Notification.Builder(this)

		        	//TODO translate
		            .setContentTitle(getResources().getString(R.string.app_message))
		            .setContentText(getResources().getString(R.string.new_tickets))
		            .setSmallIcon(R.drawable.notif)
		            .setContentIntent(pIntent)
		            .setLights(Color.RED, 500, 500)
			        .setVibrate(pattern)
		         
		            .build();

		        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		        notificationManager.notify(nbNotif, mNotification);				
				nbNotif++;
					
			}
			
			SystemClock.sleep(60000*1); // pause 1 min
			// Ca freeze la vm sans pause de toute manière
			
			
		}
		    
	}

}
