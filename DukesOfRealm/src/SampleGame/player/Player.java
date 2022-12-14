package SampleGame.player;


import SampleGame.Settings;
import SampleGame.army.*;
import SampleGame.army.Soldier.SoldierType;
import SampleGame.tiles.Castle;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Player class in charge of taking care of player actions, most methods implemented in subclasses Human and AI
 * 
 * @author elliotrenel
 *
 */

public class Player {
	public enum PlayerType{H,C,V;}
	
	protected String name;
	protected Queue<Castle> owned_castle;
	protected Queue<Soldier> moving_soldiers;
	protected PlayerType type;
	protected Image castleImage;
	protected Color playerColor;
	
	/**
	 * Class constructor.
	 * Create a player with an initial set of castles.
	 * 
	 * @param name A String containing the name of the player (Human or AI)
	 * @param initial_castles Array of initial castles
	 */
	protected Player(String name,Castle[] initial_castles) {
		this.name = name;
		this.owned_castle = new LinkedList<Castle>();
		this.moving_soldiers = new LinkedList<Soldier>();
		
		for(Castle c : initial_castles)
			this.owned_castle.add(c);
			
	}
	
	/**
	 * Second constructor.
	 * Create a player without initial castles
	 * 
	 * @param name A String containing the name of the player (Human or AI)
	 */
	protected Player(String name) {
		this.name = name;
		this.owned_castle = new LinkedList<Castle>();
		this.moving_soldiers = new LinkedList<Soldier>();
		
		
	}
	
	/**
	 * A void constructor used when creating a "VoidPlayer" object.
	 */
	protected Player() {
		this.name = "Neutral";
		this.owned_castle = new LinkedList<Castle>();
		this.moving_soldiers = new LinkedList<Soldier>();
		this.castleImage = Settings.CastleImages[0];
		this.playerColor = Color.BLACK;
		this.type=PlayerType.V;
		
	}
	
	/**
	 * Getter for name property of object.
	 * 
	 * @return String : the name of the player
	 */
	public String getName() {
		return name;
	}
	
	public Image getCastleImage() {
		return castleImage;
	}
	
	public void setCastleImageAndColor(Image castleImage) {
		this.castleImage = castleImage;
		PixelReader pixRd = castleImage.getPixelReader();
		playerColor = pixRd.getColor(30, 25);
			
	}
	
	public Color getColor() {
		return playerColor;
	}
	
	/**
	 * Getter for the type of player.
	 * 
	 * @return Player_Type : the type of player
	 */
	public PlayerType getType() {
		return type;
	}
	
	/**
	 * Add a new castle to the list of owned castles by the player.
	 * 
	 * @param c The castle to add.
	 */
	public void addCastle(Castle c) {
		if(!this.owned_castle.contains(c))
			this.owned_castle.add(c);
	}
	
	/**
	 * Revoke a castle ownership
	 * 
	 * @param c The castle lost by the player
	 */
	public void removeCastle(Castle c) {
		if(this.owned_castle.contains(c))
			this.owned_castle.remove(c);
	}
	
	/**
	 * Check if a specific castle is owned by the player
	 * 
	 * @param c The castle to test
	 * @return True if it is owned by the player, false otherwise.
	 */
	public boolean isMine(Castle c) {
		return owned_castle.contains(c);
	}
	
	/**
	 * Add a soldier to the player's moving army
	 * @param s The soldier to add
	 */
	public void addToMovingArmy(Soldier s) {
		moving_soldiers.add(s);
	}

	/**
	 * Remove a soldier to the player's moving army
	 * @param s The soldier to remove
	 */
	public void removeFromMovingArmy(Soldier s) {
		moving_soldiers.remove(s);
	}
	
	/**
	 * What the player do (mostly used by the AI subclass)
	 */
	protected void doStuff() {}
	
	/**
	 * Create the menu for a Specific castle
	 * @param castle The castle we want the menu of
	 * @return The ContextMenu object created to be used
	 */
	public ContextMenu giveMenu(Castle castle) {
		return giveMenuInfo(castle);
	}
	

	public ContextMenu giveMenuInfo(Castle castle) {
		ContextMenu menu = new ContextMenu();
		MenuItem infos = new MenuItem();		
		infos.setText("Owner : "+name+"\n"
        	+ "Treasure : "+castle.getTreasure()+"\n"
        	+ "Castle Level :"+castle.getLevel()+" ("+castle.getLevel()*Settings.CASTLE_REVENUE+" coins per round)\n"
        	+ "Army count : \n"
        	+ "\t> Stinger : "+castle.getNbTroupe(SoldierType.P)+"\n"
        	+ "\t> Knights : "+castle.getNbTroupe(SoldierType.C)+"\n"
        	+ "\t> Onagra : "+castle.getNbTroupe(SoldierType.O)+"\n"
        );
		menu.getItems().add(infos);
		return menu;
	}
	
	/**
	 * Update function called at each turn
	 */
	public boolean update() {
		if(type==PlayerType.V)
			return true;
		LinkedList<Soldier> toDelete = new LinkedList<Soldier>();
		if(owned_castle.isEmpty() && moving_soldiers.isEmpty()) {
			return false;
		}
		
		doStuff();
		
		if(!moving_soldiers.isEmpty()) {
			for(Soldier s : moving_soldiers) {
				if(s.isMoving())
					s.updateRound();
				else
					toDelete.add(s);
			}
		}
		for(Soldier s : toDelete)
			moving_soldiers.remove(s);
		return true;
	}

	public void won() {
		for(Soldier s: moving_soldiers)
			s.removeFromLayer();
	}

}
