/*
 	GlpiConnector.java is part of :
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
import java.util.ArrayList;
import java.net.URI;
import java.util.HashMap;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;


/** 
 * 
 * This class allows you to connect to GLPI and the webservice 
 * It has everything to get tickets, close them and to log yourself into GLPI
 * @author anthonyrey
 *
 */

public class GlpiConnector
{
	
	@SuppressWarnings("unused")
	private String login,srvIp,pwd;
	
	private ArrayList<Tickets> list;
	private XMLRPCClient client;
    private URI uri;
    
    private String session;
    
   
    
    private static int nbTickets = 0;
    
       
  	/** 
  	 * Allows an user to connect
  	 * This constructor is intended to be used when no sessions has been initialized yet
  	 * 
  	 * @param srvIp 
	 * Server IP
	 * 
	 * @param login 
	 * User login
	 * 
	 * @param pwd 
	 * User Password
	 * 
  	 */
	public GlpiConnector(String srvIp, String login, String pwd)
	{
		list = new ArrayList<Tickets>();
		
		this.login = login;
		this.pwd = pwd;
		this.srvIp = srvIp;
		
		uri = URI.create("http://"+srvIp+"/glpi/plugins/webservices/xmlrpc.php");
        client = new XMLRPCClient(uri);
	
	}
	
	
	/**
	 * 
	 * Allows an user to connect
  	 * This constructor is intended to be used when a session has been already initialized
  	 * 
	 * @param srvIp 
	 * Server IP
	 * 
	 * @param login 
	 * User login
	 * 
	 * @param pwd 
	 * User Password
	 * 
	 * @param session 
	 * Session ID
	 * 
	 */
	public GlpiConnector(String srvIp, String login, String pwd, String session)
	{
		list = new ArrayList<Tickets>();
		
		this.login = login;
		this.pwd = pwd;
		this.srvIp = srvIp;
		this.session = session;
		
		
		uri = URI.create("http://"+srvIp+"/glpi/plugins/webservices/xmlrpc.php");
        client = new XMLRPCClient(uri);
	
	}
	
	/**
	 * Used to get the session ID
	 * 
	 * @return
	 * The session ID
	 */
	public String getSession() {
		return session;
	}
	

	
	/** 
	 * Allows to create a link to GLPI, actually gets the session from what you filled with the constructor
	 * 
	 * 
	 * @throws XMLRPCException 
	 */
	
	
	public void setSessionAccordingToInfos() throws XMLRPCException 
	{
	  	        	 
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("login_name", login);
    	params.put("login_password", pwd);
    		
    	@SuppressWarnings({ "rawtypes" })
		HashMap data = (HashMap) client.call("glpi.doLogin", params);
    	
	 	session = data.get("session").toString();

	}
	
	/**
	 * 
	 * Check the version of Webservice and GLPI
	 * 
	 * @return true if Webservice and GLPI are at the right versions
	 * @throws XMLRPCException
	 */
	public boolean versionCheck() throws XMLRPCException 
	{
	  	        	 
    	HashMap<String, String> params = new HashMap<String, String>();	
    	@SuppressWarnings({ "rawtypes" })
		HashMap data = (HashMap) client.call("glpi.test", params);
    	
	 	if(data.get("glpi").toString().contains("0.84") && data.get("webservices").toString().matches("1.4.0") || data.get("webservices").toString().matches("1.4.1") ){
	 		
	 		return true;
	 		
	 	}else{

	 		return false;
	 	}
	}
	

	
	/**
	 * 
	 * Add one and only one ticket to the ticket ArrayList "list"
	 *  
	 * @param id 
	 * GLPI's id of the required ticket
	 * 
	 * 
	 * @throws XMLRPCException 
	 */
	
	public void addTicketsToList(String id) throws XMLRPCException {


    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("session", session);
    	params.put("ticket", id);
    	params.put("id2name", "");
    	
    	@SuppressWarnings({ "rawtypes" })
		HashMap data = (HashMap) client.call("glpi.getTicket", params);
    	
    	list.add(new Tickets( Integer.parseInt(id) , data.get("name").toString(), data.get("content").toString(), data.get("date").toString(), data.get("date_mod").toString(), data.get("users_name_recipient").toString(), data.get("status").toString()  ));
	 
	}
	
	
	/**
	 * 
	 * Add every tickets to the ticket ArrayList "list"
	 * (Except those who are closed or not assigned to this particular user)
	 * 
	 * @throws XMLRPCException 
	 */
		
	public void addTicketsToList() throws XMLRPCException {
	
		HashMap<String, String> params = new HashMap<String, String>();
    	params.put("session", session);
    	params.put("order", "id");
    	params.put("id2name", "");
    	//sinon on a que 20 tickets qui s'affichent
        params.put("limit", "25000" );
        params.put("status", "2");
    	
    	

		Object ob = client.call("glpi.listTickets", params);

		Object liste[] = (Object[]) ob; // On met le resultat dans un tableaux 
				
		// On parse les hashmaps du tableau 
		
		for(Object obj : liste){ 
			@SuppressWarnings("rawtypes")
			HashMap parseResult = (HashMap) obj;
						 				
			if( hasTicketToBeDisplayed (parseResult.get("id").toString()) /*&& !isTkClosed(parseResult.get("id").toString()) && !isTkNew(parseResult.get("id").toString())*/){
			
				list.add(new Tickets(Integer.parseInt(parseResult.get("id").toString()), parseResult.get("name").toString(), parseResult.get("content").toString(), parseResult.get("date").toString(), parseResult.get("date_mod").toString(), parseResult.get("users_name_recipient").toString(), parseResult.get("status").toString() ));
				
			}
			
		}
		
		nbTickets = list.size();
		
	}
	
	public void addClosedTicketsToList() throws XMLRPCException {
		
		HashMap<String, String> params = new HashMap<String, String>();
    	params.put("session", session);
    	params.put("order", "id");
    	params.put("id2name", "");
    	//sinon on a que 20 tickets qui s'affichent
        params.put("limit", "25000" );
        params.put("status", "5");
    	
    	

		Object ob = client.call("glpi.listTickets", params);

		Object liste[] = (Object[]) ob; // On met le resultat dans un tableaux 
				
		// On parse les hashmaps du tableau 
		
		for(Object obj : liste){ 
			@SuppressWarnings("rawtypes")
			HashMap parseResult = (HashMap) obj;
						 			
			list.add(new Tickets(Integer.parseInt(parseResult.get("id").toString()), parseResult.get("name").toString(), parseResult.get("content").toString(), parseResult.get("date").toString(), parseResult.get("date_mod").toString(), parseResult.get("users_name_recipient").toString(), parseResult.get("status").toString() ));
	
			
		}		
	}

	
	/**
	 * Used to tell whether a ticket should or should not be showed to the current user
	 * 
	 * @param id 
	 * GLPI's id of the required ticket
	 * 
	 * @return
	 * TRUE if the ticket is to be displayed
	 * 
	 *@throws XMLRPCException 
	 */
		
	private boolean hasTicketToBeDisplayed(String id) throws XMLRPCException {
		
		
		boolean leTest = false;
		
    	HashMap<String, String> params = new HashMap<String, String>();

    	params.put("session", session);
    	params.put("ticket" , id);
    	params.put("id2name", "true");
    
    	@SuppressWarnings({ "rawtypes", "unchecked" })
		HashMap<String, Object> root = (HashMap) client.call("glpi.getTicket", params);
		@SuppressWarnings("unchecked")
		HashMap<String, Object> users = (HashMap <String, Object>) root.get("users");
		Object assign[] =  (Object[]) users.get("assign");
		
		for(Object obj : assign){
			
			@SuppressWarnings("unchecked")
			HashMap<String, Object> assigned = (HashMap<String, Object>) obj;
			
			if(assigned.get("users_name").equals(login)){
				
				leTest = true;
				break;
				
			}
			
		}
	
        return leTest;	
	}
	
	
	/** 
	 * Returns the arrayList containing all of the inserted tickets
	 * 
	 * @see addTicketsToList()
	 * 
	 * @return
	 * The "list" Ticket ArrayList
	 */
	public ArrayList<Tickets> getTickets()
	{
		
		return list;
	}
	
	
	/**
	 * Return the ticket at the given position in "list"
	 * 
	 * @param position
	 * The position of the ticket in "list"
	 * 
	 * @return
	 * A ticket
	 */
	public Tickets getTickets(int position)
	{
		
		return list.get(position);
	}
	

	

	
	/**
	 * 
	 * Allows to close a ticket
	 * 
	 * @param id
	 * The GLPI's id for the ticket to close
	 * 
	 * @param solution
	 * The solution to the problem
	 * 
	 * @throws
	 * 
	 */
	public void solveTicket(String id, String solution ) throws XMLRPCException
	{

			 
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("session", session);
    	params.put("ticket", id);
    	params.put("solution", solution);

		client.call("glpi.setTicketSolution", params);
       
	}
	
	
	
	/*
	private boolean isTkClosed(String id) throws XMLRPCException{
		
		boolean bool = false;
	

        	 HashMap<String, String> params = new HashMap<String, String>();
	        	params.put("session", session);
	        	params.put("ticket", id);
	        	
	        	
		
				@SuppressWarnings({ "rawtypes" })
				HashMap data = (HashMap) client.call("glpi.getTicket", params);
	 				
					// 5 = resolu 
				   // 6 = cloturé
					
	 				if("5".equals(data.get("status").toString())){
	 					
	 					bool = true;
	 					}
	 				else if("6".equals(data.get("status").toString())){
	 					
	 					bool = true;
	 				}
			 	
        return bool;	
	}

	private boolean isTkNew(String id) throws XMLRPCException {
		
		boolean bool = false;
	      
        	 HashMap<String, String> params = new HashMap<String, String>();
	        	params.put("session", session);
	        	params.put("ticket", id);
	        	
	        	
		
				@SuppressWarnings({ "rawtypes" })
				HashMap data = (HashMap) client.call("glpi.getTicket", params);
	 				
				
					// 1 = new 
	 				if("1".contains(data.get("status").toString())){
	 					
	 					bool = true;
	 				}
			 		
        return bool;	
	}
	
	*/
	
	
	/**
	 * Just dump the content of list ArrayList
	 * 
	 */
	public void dumpList(){
		
		list.clear();
	}

	/**
	 * 
	 * Used to get the number of tickets
	 * 
	 * @param id
	 * @return the count of tickets
	 */
	public static int getNbTickets() {
		return nbTickets;
	}


}
