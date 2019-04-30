package com.td.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/**
 * Ship class stores the state of a ship character.
 */
class Ship {

  private static final Texture TEXTURE = new Texture(Gdx.files.internal("ship.png"));

  private final Sprite sprite;    // Ship configuration
  private final Circle range;     // Encircles the tower and acts as a detection range

  private Enemy target;           // Identifies the current target for the tower
  private Missile missile;        // Every ship has a missile to fire - can be created/deleted for repeat firing

  /**
   * Simple constructor for a Ship object.
   *
   * @param spawnX the x-position of the collision box for this tower.
   * @param spawnY the y-position of the collision box for this tower.
   */
  Ship(int spawnX, int spawnY) {
    sprite = new Sprite(TEXTURE);
    sprite.setPosition(spawnX, spawnY);

    range = new Circle(sprite.getX(), sprite.getY(), 300);
  }

  /**
   * Runs the ship for a single frame.
   *
   * @param wave the current state of the wave for this frame
   */
  void run(Wave wave) {
    // If the ship doesn't have a target, look for one
    if (this.target == null) {
      target = scanForTarget(wave.getEnemies());
    } else {
      // Check the target is still in range
      if (range.overlaps(this.target.getZone())) {
        engageTarget();
      } else {
        disengageTarget();
      }
    }
  }

  /**
   * Ship engages with a target - includes rotating towards the target and firing a missile at the
   * target.
   */
  private void engageTarget() {
    Vector2 shipVector = new Vector2(sprite.getX(), sprite.getY()); // Vector for the ship
    Vector2 targetVector = target.getVector();                      // Vector for the target

    float opp = targetVector.x - shipVector.x;  // Length of the opposite side
    float adj = shipVector.y - targetVector.y;  // Length of the adjacent side

    float angle = MathUtils.radiansToDegrees * MathUtils
        .atan2(opp, adj); // Arc tan to find angle between ship & target

    sprite.setRotation(angle);

    if (missile == null) {
      missile = new Missile(sprite.getX(), sprite.getY());
    }

    Vector2 missileDestination = targetVector
        .sub(shipVector); // Determines the vector for the missile to head towards
    missile.updatePosition(missileDestination);

    if (missile.getCollisionZone().overlaps(target.getZone())) { // TODO This currently doesn't work
      missile = new Missile(sprite.getX(), sprite.getY());
    }

  }

  /**
   * Removes engagement from target.
   */
  private void disengageTarget() {
    missile = null;
    target = null;
    sprite.setRotation(0);
  }

  /**
   * Given a wave of enemies, will identify the first enemy to be in the range of the ship and set
   * it as target.
   *
   * @param enemies the current wave of enemies
   * @return Enemy if a target is found, otherwise null
   */
  private Enemy scanForTarget(List<Enemy> enemies) {
    return enemies.stream()                                     // Java 8 stream
        .filter(enemy -> enemy.getZone().overlaps(this.range))  // Filter by a given predicate
        .findFirst()                                            // Only return the first
        .orElse(null);                                    // Return null if none found
  }

  /**
   * Renders the Sprite on the SpriteBatch.
   *
   * @param batch SpriteBatch to be rendered onto
   */
  void batchDraw(SpriteBatch batch) {
    if (missile != null) {
      missile.batchDraw(batch);
    }
    sprite.draw(batch);
  }
}
