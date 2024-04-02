/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package esmaieeli.telegram.mafia.backend;

/**
 *
 * @author Amir Mohammad Esmaieeli
 */
public class Role {

    public String name;
    public String picName = " ";

    public Role(String passed_name, String passed_picName) {
        name = passed_name;
        picName = passed_picName;
    }

}
