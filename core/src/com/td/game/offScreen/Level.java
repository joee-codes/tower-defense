package com.td.game.offScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.td.game.onScreen.Ship;
import java.util.Random;

/**
 * Level class stores the state of a level. By having this in a Level class rather than the
 * GameManager, it means that different levels could be configured in the future.
 */
public class Level {

  private static final float SPAWN_PROBABILITY = 0.005f;   // The change of an enemy spawning

  private LevelManager levelManager;    // A levelManager of enemies for this level
  private Ship ship;    // A single tower for this level (will be more in further releases)
  private Random random = new Random();   // Used to generate random number for spawn prob

  /**
   * Constructor for a Level.
   *
   * @param waveSize configurable levelManager size
   */
  public Level(int waveSize) {
    this.levelManager = new LevelManager(20, -64, 476);
    this.ship = new Ship(869, 700);
  }

  /**
   * Method invoked by GameManager with the rendering of each frame.
   */
  public void update() {
    levelManager.cleanUp();   // Cleans up enemy enemies that are dead or have left the map
    levelManager.removeEnemyIfDead();

    float randomFloat = this.random.nextFloat();    // Determine a spawn probability

    // Spawns enemies onto the map
    if (randomFloat < SPAWN_PROBABILITY) {
      levelManager.addEnemy();    // Adds a new enemy to the levelManager
    }

    levelManager.updatePositions(100 * Gdx.graphics.getDeltaTime());  // Updates the position of the enemies

    ship.run(levelManager);   // Ship looks for enemies within it's range
  }

  /**
   * Method invoked by GameManager and calls rendering methods on levelManager and ship.
   *
   * @param batch SpriteBatch passed from GameManager ensures all components are drawn on the same
   * batch
   */
  public void draw(SpriteBatch batch) {
    this.levelManager.batchDraw(batch);
    this.ship.batchDraw(batch);
  }
}
