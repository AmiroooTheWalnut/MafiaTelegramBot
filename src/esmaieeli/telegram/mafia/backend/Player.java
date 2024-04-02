/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package esmaieeli.telegram.mafia.backend;

/**
 *
 * @author Amir Mohammad Esmaieeli
 */
public class Player {
    public String name;
    public transient Role role;
    public transient boolean hasSeenRole;
    
    public Player(String passed_name){
        name=passed_name;
    }
}
