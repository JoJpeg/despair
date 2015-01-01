package de.lfstudios.game.core.player;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * @author vwiebe
 */
public class Map
{
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer mapRenderer;
	private int mapPixelWidth;
	private int mapPixelHeight;

	private final static int MAP_SCALE = 4;
	private static final float WORLD_TO_BOX = 0.01f;
	private static final float BOX_TO_WORLD = 100f;

	private static final String MAP_DARK = "dark";
	private static final String MAP_LIGHT = "light";
	private static final String MAP_COLLISION_DARK = "collision_dark";
	private static final String MAP_COLLISION_LIGHT = "collision_light";
	private static final String MAP_HOLE_DARK = "hole_dark";
	private static final String MAP_HOLE_LIGHT = "hole_light";

	public Map()
	{
		this.world = new World(new Vector2(0, 0), true);
		this.debugRenderer = new Box2DDebugRenderer();
		this.setupMap();
	}

	private void setupMap()
	{
		this.tiledMap = new TmxMapLoader().load("game/map/map.tmx");
		this.tiledMap.getLayers().get(this.MAP_LIGHT).setVisible(false);
		this.mapRenderer = new OrthogonalTiledMapRenderer(this.tiledMap, this.MAP_SCALE);

		this.mapPixelWidth = this.tiledMap.getProperties()
										  .get("width", Integer.class) *
							 this.tiledMap.getProperties()
										  .get("tilewidth", Integer.class) *
							 this.MAP_SCALE;

		this.mapPixelHeight = this.tiledMap.getProperties()
										   .get("height", Integer.class) *
							  this.tiledMap.getProperties()
										   .get("tileheight", Integer.class) *
							  this.MAP_SCALE;
	}

	/**
	 *
	 * @param camera
	 */
	public void updatePhysics(OrthographicCamera camera)
	{
		Matrix4 cameraCopy = camera.combined.cpy();
		this.debugRenderer.render(this.world,
								  cameraCopy.scl(this.BOX_TO_WORLD));
		this.world.step(1 / 60f, 6, 2);
	}

	/**
	 *
	 */
	public void dispose()
	{
		this.getTiledMap().dispose();
		this.getMapRenderer().dispose();
	}

	/**
	 *
	 * @param player
	 */
	public void add(Player player)
	{
		player.getBodyDef().position.set(new Vector2(player.getPosX() * this.getWorldToBox(), (player.getPosY() + 300) * this.getWorldToBox()));
		player.setBody(this.getWorld().createBody(player.getBodyDef()));

		CircleShape playerShape = new CircleShape();
		playerShape.setRadius(0.2f);
		playerShape.setPosition(new Vector2(0.65f, 0.1f));
		player.getBody().createFixture(playerShape, 0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = playerShape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;

		playerShape.dispose();
	}

	/**
	 *
	 * @return
	 */
	public int getMapPixelWidth()
	{
		return this.mapPixelWidth;
	}

	/**
	 *
	 * @return
	 */
	public int getMapPixelHeight()
	{
		return this.mapPixelHeight;
	}

	/**
	 *
	 * @return
	 */
	public int getMapScale()
	{
		return this.MAP_SCALE;
	}

	/**
	 *
	 * @return
	 */
	public TiledMap getTiledMap()
	{
		return this.tiledMap;
	}

	/**
	 *
	 * @return
	 */
	public OrthogonalTiledMapRenderer getMapRenderer()
	{
		return this.mapRenderer;
	}

	/**
	 *
	 * @return
	 */
	public World getWorld()
	{
		return this.world;
	}

	/**
	 *
	 * @return
	 */
	public static float getWorldToBox()
	{
		return WORLD_TO_BOX;
	}

	/**
	 *
	 * @return
	 */
	public static float getBoxToWorld()
	{
		return BOX_TO_WORLD;
	}

	/**
	 *
	 * @return
	 */
	public static String getMapDark()
	{
		return MAP_DARK;
	}

	/**
	 *
	 * @return
	 */
	public static String getMapLight()
	{
		return MAP_LIGHT;
	}

	/**
	 *
	 * @return
	 */
	public static String getMapCollisionDark()
	{
		return MAP_COLLISION_DARK;
	}

	/**
	 *
	 * @return
	 */
	public static String getMapCollisionLight()
	{
		return MAP_COLLISION_LIGHT;
	}

	/**
	 *
	 * @return
	 */
	public static String getMapHoleDark()
	{
		return MAP_HOLE_DARK;
	}

	/**
	 *
	 * @return
	 */
	public static String getMapHoleLight()
	{
		return MAP_HOLE_LIGHT;
	}
}
