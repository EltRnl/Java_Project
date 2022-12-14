package SampleGame.army.soldiers;

import SampleGame.Settings;
import SampleGame.army.Soldier;
import SampleGame.player.Player;
import SampleGame.tiles.Castle;

/**
 * Class for creating the soldier type "Piquier"
 * @author elliotrenel
 *
 */
public class Piquier extends Soldier {
	
	public static final int COST = 100;
	public static final int HEALTH = 1;
	public static final int DAMAGE = 1;
	
	/**
	 * Piquier constructor with the corresponding speeds/health/...
	 * 
	 * @param layer The global visual window
	 * @param x The sprite x position (his home position)
	 * @param y The sprite y position (his home position)
	 * @param duke The Piquier's owner name
	 */
	public Piquier(int x, int y, Player owner) {
		super(Settings.PiquierImage, x, y, owner);
		
		this.owner = owner;
		this.speed = 2;
		this.health = HEALTH;
		this.damage = DAMAGE;
		this.cost = COST;
		this.time_prod = 5;
		this.type = SoldierType.P;
		
	}
	
	/**
	 * Second constructor from Castle
	 * 
	 * @param layer The global visual window
	 * @param home The soldier's home
	 */
	public Piquier(Castle home) {
		super(Settings.PiquierImage, home.getDoorImg().getX_out(), home.getDoorImg().getY_out(), home.getOwner());
		
		this.owner = home.getOwner();
		this.speed = 2;
		this.health = 1;
		this.damage = 1;
		this.cost = 100;
		this.time_prod = 5;
		this.type = SoldierType.P;
		
	}

	@Override
	public Soldier trainNew() {
		return new Piquier(this.getX(),this.getY(), this.owner);
	}
	
}
