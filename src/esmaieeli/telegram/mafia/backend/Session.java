/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package esmaieeli.telegram.mafia.backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amir Mohammad Esmaieeli
 */
public class Session {

    public ArrayList<Player> players;
    public ArrayList<Role> roles;
    public transient Date startSessionDate;
    public transient Method gUIUpdateMethod;
    public transient Object parentGUI;

    public Session(ArrayList<Player> passedPlayers, ArrayList<Role> passed_roles, Method passed_gUIUpdateMethod, Object passed_parentGUI) {
        players = passedPlayers;
        roles = passed_roles;
        gUIUpdateMethod = passed_gUIUpdateMethod;
        parentGUI = passed_parentGUI;
        assignRolesToPlayers();
        Instant nowInstant = Instant.now();
        startSessionDate = Date.from(nowInstant);
    }

    private void assignRolesToPlayers() {
        ArrayList<Role> tempRoles = new ArrayList(roles);
        Random rnd = new Random(System.currentTimeMillis());
        Collections.shuffle(players);
        for (int i = 0; i < players.size(); i++) {
            int roleIndex = (int) (Math.floor(rnd.nextDouble() * (tempRoles.size())));
            players.get(i).role = tempRoles.get(roleIndex);
            tempRoles.remove(roleIndex);
        }
    }

    public static Session createSession(String players, String roles, String rolePics, Method gUIMethod, Object parentGUIClass) {
        ArrayList<Player> playersAL = new ArrayList();
        ArrayList<Role> rolesAL = new ArrayList();

        String[] playerLines = players.split("\n");
        for (int i = 0; i < playerLines.length; i++) {
            playersAL.add(new Player(playerLines[i]));
        }

        String[] roleLines = roles.split("\n");
        rolePics=rolePics.replace("\n", "!\n!");
        String[] rolePicsLines = rolePics.split("[\\r\\n]+");
        for (int i = 0; i < roleLines.length; i++) {
            if(i==0){
                rolesAL.add(new Role(roleLines[i], rolePicsLines[i].substring(0, rolePicsLines[i].length()-1)));
            }else if(i==roleLines.length-1){
                rolesAL.add(new Role(roleLines[i], rolePicsLines[i].substring(1, rolePicsLines[i].length())));
            }else{
                rolesAL.add(new Role(roleLines[i], rolePicsLines[i].substring(1, rolePicsLines[i].length()-1)));
            }
        }

        Session output = new Session(playersAL, rolesAL, gUIMethod, parentGUIClass);

        return output;
    }

    public void updateSessionGUI() {
        if (gUIUpdateMethod != null) {
            try {
                gUIUpdateMethod.invoke(parentGUI);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
