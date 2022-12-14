package SampleGame.tiles;

import SampleGame.Settings;
import SampleGame.Sprite;
import SampleGame.army.*;
import SampleGame.army.Factory.ProductionType;
import SampleGame.army.Soldier.SoldierType;
import SampleGame.army.soldiers.*;
import SampleGame.player.*;
import SampleGame.player.Player.PlayerType;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


/**
 * An entity of the game: Controlling a castle as a player allows to produce and stack units, and to order armies to attack other castles.
 * @author thdupont
 *
 */
public class Castle extends Sprite{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1081642180349182515L;


	public enum Orientation{
		N,
		E,
		S,
		W;
	}
	
	private Player owner;
	private int treasure, level;
	private Hashtable<SoldierType, Queue<Soldier>> army;
	private Queue<Order> orders;
	private Order current_order;
	@SuppressWarnings("unused")
	private final Orientation OrientationDoor;						/* Not used*/
	private Door doorImg;
	private Factory fact;
	private Random rand = new Random();
	
	/**
	 * 
	 * Creates a new castle in the layer.
	 * 
	 * @param x	The x coordinate of the castle in the window
	 * @param y The y coordinate of the castle in the window
	 * @param owner The player who owns the castle
	 * @param treasure The base money for the castle
	 * @param door	The orientation of the exit
	 * @param fact The class who allows production @see Factory.java
	 * @return A new castle printed on the window
	 * 
	 **/
	
	public Castle(int x, int y, Player owner, int treasure, Orientation OrientationDoor, Factory fact) {		
		super(Settings.field, owner.getCastleImage(), x, y );
		this.owner = owner;
		owner.addCastle(this);
		this.treasure = treasure;
		
		army = new Hashtable<SoldierType, Queue<Soldier>>();
		army.put(SoldierType.P, new LinkedList<Soldier>());
		army.put(SoldierType.C, new LinkedList<Soldier>());
		army.put(SoldierType.O, new LinkedList<Soldier>());
		this.OrientationDoor = OrientationDoor;
		
		ImageView view_door;
		
		
		switch(OrientationDoor) {
		
		case N:
			this.setDoorImg(new Door(this.getX()+1, this.getY()-(2*Settings.height_DoorImage)-2));
			break;
			
		case E:
			this.setDoorImg(new Door(this.getX()+((int)this.getWidth()/2), this.getY()));
			view_door = this.doorImg.getView();
			view_door.setRotate(90);
			break;
			
		case S:
			this.setDoorImg(new Door(this.getX()+2, this.getY()+(2*Settings.height_DoorImage)+2));
			view_door = this.doorImg.getView();
			view_door.setRotate(180);
			break;
			
		case W:
			this.setDoorImg(new Door(this.getX()-((int)this.getWidth()/2), this.getY()));
			view_door = this.doorImg.getView();
			view_door.setRotate(270);
			break;
			
		}
		
		
		this.fact = fact;
		fact.setCastle(this);
		this.orders = new LinkedList<Order>();
		this.current_order = null;
		this.level = 1;
		
		updateUI();
	}
	
		
	/** 
	 * Checks if there is a new soldier to add from production and then adds or not the right soldier to the army
	*/
	private void addToArmy() {
		Pair<ProductionType,SoldierType> result = fact.update();
		if(result!=null) {
			if(result.getKey()==ProductionType.U)
				this.level++;
			else {
				SoldierType t = result.getValue();
				Soldier s;
				switch (t) {
				case P:
					addToArmy(new Piquier(this));
					break;
				case C:
					addToArmy(new Chevalier(this));
					break;
				case O:
					addToArmy(new Onagre(this));
					break;
				default:
					break;
				}
			}
		}
	}
	
	/**
	 * Adds a soldier to the queue "army".
	 * 
	 * @param s The soldier to add
	*/
	public void addToArmy(Soldier s) {
		army.get(s.getType()).add(s);
	}
	
	public void addToArmy(SoldierType t, int nb) {
		for(int i=0; i<nb; i++) {
		switch (t) {
		case P:
			army.get(t).add(new Piquier(this));
			break;
		case C:
			army.get(t).add(new Chevalier(this));
			break;
		case O:
			army.get(t).add(new Onagre(this));
			break;

		default:
			break;
		}
		}
	}
	
	/**
	 * Remove a specific type of soldiers from the castle army
	 * 
	 * @param t The type of soldier
	 * @return The removed soldier
	 */
	private Soldier removeFromArmy(SoldierType t) {
		return army.get(t).remove();
	}
	
	/**
	 * Remove a specific soldier from the castle army
	 * 
	 * @param s The soldier object to remove
	 */
	private void removeFromArmy(Soldier s) {
		army.get(s.getType()).remove(s);
	}
	
	/**
	 * When the castle gets attacked by an ennemy soldier
	 * 
	 * @param s the attacker
	 */
	public void getAttacked(Soldier s) {
		if(this.noMoreArmy()) {
			this.owner.removeCastle(this);
			this.owner = s.getOwner();
			addToArmy(s);

			s.setX(this.getDoorImg().getX_out());
			s.setY(this.getDoorImg().getY_out());
			
			this.owner.addCastle(this);
			this.fact.resetFactory();
			this.changeImage(owner.getCastleImage());
		}else {
			Soldier challenger = this.chooseChallenger();
			while(s.attackSoldier(challenger) && !this.noMoreArmy()) {
				this.removeFromArmy(challenger);
				challenger = this.chooseChallenger();
			}
			if(challenger.getHealth()==0) this.removeFromArmy(challenger);
		}
	}
	
	/**
	 * Choose a random opponent in the army to fight an intruder
	 * @return The chosen warrior
	 */
	private Soldier chooseChallenger() {
		SoldierType t = SoldierType.values()[rand.nextInt(3)];
		while(army.get(t).isEmpty())
			t = SoldierType.values()[rand.nextInt(3)];
		Soldier s = this.removeFromArmy(t);
		this.addToArmy(s);
		return s;
	}


	/**
	 * Check if the castle has any soldier left.
	 * @return True if there are no more soldiers, false if it found at least one.
	 */
	public boolean noMoreArmy() {
		for(SoldierType t : SoldierType.values())
			if(!army.get(t).isEmpty())
				return false;
		return true;
	}
	
	
	/**
	 * Sets a new order
	 * 
	 * @param order The new order to assign
	 */
	public void giveOrder(Order order) {
		if(order.getTarget()==this) return;
		if(order.getTroops()<=getNbTroupe(order.getType())) {
			this.orders.add(order);
		}else {
			System.out.println("Not enough troops");
		}
		
	}	
	
	/**
	 * Sends maximum 3 soldiers through the gates to attack the castle pointed by order
	 * Puts order back to null if no more troops to send.
	 * 
	 */
	
	private void executeOrder() {
		if(current_order==null && !orders.isEmpty()) {
			current_order=orders.remove();
		}
		if(current_order!=null){
			if (current_order.getTroops()!=0) {
				for(int i=0; i<3 && current_order.getTroops()>0 && !army.get(current_order.getType()).isEmpty();i++, current_order.sendTroops()) {
					//System.out.println("Sending Troops "+this.getNbTroupe(current_order.getType()));
					Soldier s = removeFromArmy(current_order.getType());
					owner.addToMovingArmy(s);
					s.executeOrder66(current_order.getTarget());
				}
			}else {
				this.current_order = null;
			}
		}
	}
	
	/**
	 * Getter for owner's name
	 * @return String : the player's name
	 */
	public String getOwnerName() {
		return owner.getName();
	}

	/**
	 * Set castle's owner
	 * @param owner The Player object 
	 */
	public void setOwner(Player owner) {
		this.owner = owner;
	}
	
	/**
	 * Get castle's owner
	 * @return Player : the owner of the castle
	 */
	public Player getOwner() {
		return this.owner;
	}

	/**
	 * Getter for Castle level
	 * @return The current castle level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Setter for Castle level
	 * @param level The level to set the castle
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	/**
	 * Getter for army size
	 * @param t The type of soldier
	 * @return int : Size of the current army
	 */
	public int getNbTroupe(SoldierType t) {
		return army.get(t).size();
	}
	
	/**
	 * Getter for treasure content
	 * @return The castle current treasure
	 */
	public int getTreasure() {
		return treasure;
	}


	/**
	 * Add a training order to the factory
	 * @param type The soldier you want to train
	 * @param quantity The quantity of this specific soldiers you want to train
	 * @param check A boolean set as true if you only want to know if production is possible
	 * @return True if the castle can produce the army, false if it can't
	 */
	public boolean produceArmy(SoldierType type, int quantity, boolean check) {
		Soldier soldier;
		switch (type) {
		case P:
			soldier = new Piquier(this);
			break;
		case C:
			soldier = new Chevalier(this);
			break;
		case O:
			soldier = new Onagre(this);
			break;
		default:
			return false;
		}
		int cost = (soldier.getCost()*quantity);
		if(treasure-cost>=0) {
			if(!check) {
				fact.addTraining(soldier, quantity);
				this.treasure -= cost;
			}
			return true;
		}
		else return false;	
	}
	
	/**
	 * Add a training order to the factory
	 * @param type The soldier you want to train
	 * @param quantity The quantity of this specific soldiers you want to train
	 * @return True if the castle can produce the army, false if it can't
	 */
	public boolean produceArmy(SoldierType type, int quantity) {
		return produceArmy(type, quantity, false);
	}
	
	public boolean upgradeCastle(boolean check) {
		int cost = (this.level +1)*1000;
		if(treasure-cost>=0) {
			if(!check) {
				fact.upgradeCastle();
				treasure -=cost;
			}
			return true;
		}
		return false;
	}
	
	public boolean upgradeCastle() {
		return upgradeCastle(false);
	}
	
	public boolean isUpgrading() {
		return fact.upgrade;
	}

	/**
	 * Show castle menu when the castle owner is the player, only show information if the castle
	 * is owned by an opponent.
	 */
	public void showMenu(MouseEvent event) {
		(owner.giveMenu(this)).show(this.layer,event.getScreenX(),event.getScreenY());
	}	
	

	public void showInfo(MouseEvent event) {
		(owner.giveMenuInfo(this)).show(this.layer,event.getScreenX(),event.getScreenY());
	}
	
	public void sendArmy() {
		((Human)owner).sendArmy(this);
	}
	
	public void setYourselfAsTarget(Human player) {
		Castle me = this;
		this.getView().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				switch (event.getButton().toString()) {
				case "SECONDARY":
					showInfo(event);
					break;
				case "PRIMARY":
					player.targetAcquired(me,event);
					break;
				default:
					break;
				}
				
			}
			
		});
		
	}
	
	@Override
	public void updateUI() {
		super.updateUI();
		
		this.getView().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				switch (event.getButton().toString()) {
				case "SECONDARY":
					showMenu(event);
					break;
				case "PRIMARY":
					if(owner.getType()==PlayerType.H) {
						sendArmy();
					};
					break;
				case "MIDDLE":
					System.out.println("MIDDLE");
					break;
				default:
					break;
				}
				
			}
			
		});
	}


	/**
	 * Called at each turn to update the different mechanics
	 */
	public void updateRound(){
		
		addToArmy();
		
		executeOrder();
		
		treasure += level*Settings.CASTLE_REVENUE;
		
	}


	public Door getDoorImg() {
		return doorImg;
	}


	public void setDoorImg(Door doorImg) {
		this.doorImg = doorImg;
	}


	public int howMuchICanMake(SoldierType t) {
		int cost;
		switch (t) {
		case P:
			cost = Piquier.COST;
			break;
		case C:
			cost = Chevalier.COST;
			break;
		case O:
			cost = Onagre.COST;
			break;
		default:
			cost = 2147483647;
			break;
		}
		return (int)(treasure/cost);
	}
	
}
