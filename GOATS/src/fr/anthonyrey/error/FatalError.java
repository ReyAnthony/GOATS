/*
 	FatalError.java is part of :
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
package fr.anthonyrey.error;

import fr.anthonyrey.activities.Login;
import fr.glpi.mobile.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * This activity shows a message displaying the error code when called
 * 
 * @author anthonyrey
 *
 */
public class FatalError {

	public FatalError(String e , final Activity acti ){

		  // on crée une alerte si y'a une erreur 
		  AlertDialog.Builder builder1 = new AlertDialog.Builder(acti);
		  // On lui donne un titre
		  builder1.setTitle(acti.getResources().getString(R.string.fatal_error));
		  
		  // On lui affecte le message en fonction du code d'erreur
		 builder1.setMessage(e);
	  
        builder1.setCancelable(false);
        builder1.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
          	  // Si on appuie sur OK, Ã§a ferme
                dialog.dismiss();  
                
                Intent intent = new Intent(acti, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    			acti.startActivity(intent); 
    			acti.finish();
    			  //tue le processus (et donc le background intent)
    		    android.os.Process.killProcess(android.os.Process.myPid());
    			
            }
        });
     
        AlertDialog alert11 = builder1.create();
        alert11.show(); // on l'affiche
	}
}

