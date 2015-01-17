package de.lfstudios.game.core.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.lfstudios.game.core.player.Player;
import java.util.HashSet;

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

	private HashSet<MapObject> objectSet = new HashSet<MapObject>();
	private HashSet<MapObject> visibleObjectSet = new HashSet<MapObject>();
	private Vector2 lastPlayerPos;


	public Map()
	{
		this.world = new World(new Vector2(0, 0), true);

		this.debugRenderer = new Box2DDebugRenderer(false,  // drawBodies
													false,  // drawJoints
													false,  // drawAABBs
													false,  // drawInactiveBodies
													false,  // drawVelocities
													false); // drawContacts
		this.setupMap();
	}

	private void setupMap()
	{
		this.tiledMap = new TmxMapLoader().load("game/map/map.tmx");
		this.tiledMap.getLayers().get(this.MAP_DARK).setVisible(false);
		this.tiledMap.getLayers().get(this.MAP_LIGHT).setVisible(true);
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

		this.setupCollision(this.MAP_COLLISION_LIGHT);
		this.setupCollision(this.MAP_HOLE_LIGHT);
		this.setupCollision("objects_light");
		this.setupCollision("grass_light");
		this.setupCollision("stream_light");
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
		player.getBodyDef().position.set(new Vector2(player.getPosX() * this.getWorldToBox(),
													 (player.getPosY() + 300) * this.getWorldToBox()));
		player.setBody(this.getWorld().createBody(player.getBodyDef()));

		CircleShape playerShape = new CircleShape();
		playerShape.setRadius(0.2f);
		playerShape.setPosition(new Vector2(0.65f, 0.1f));
		player.getBody().createFixture(playerShape, 0f);

//		FixtureDef fixtureDef = new FixtureDef();
//		fixtureDef.shape = playerShape;
//		fixtureDef.density = 0.5f;
//		fixtureDef.friction = 0.4f;
//		fixtureDef.restitution = 0.6f;

		playerShape.dispose();
	}

	/**
	 *
	 * @param layerName
	 */
	private void setupCollision(String layerName)
	{
		MapLayer layer = this.tiledMap.getLayers().get(layerName);

		if(layer == null)
		{
			System.out.println("layer: " + layerName + " does not exist!");
			return;
		}

		int i = 0;

		for(MapObject object : layer.getObjects())
		{
			i++;
			// polyline collision
			if(object instanceof PolylineMapObject)
			{
				BodyDef groundDef;
				Body groundBody;

				groundDef = new BodyDef();
				groundDef.type = BodyDef.BodyType.StaticBody;

				groundDef.position.set(((PolylineMapObject) object).getPolyline().getX() * this.getWorldToBox() * this.getMapScale(),
									   ((PolylineMapObject) object).getPolyline().getY() * this.getWorldToBox() * this.getMapScale());

				groundBody = world.createBody(groundDef);

				float[] vertices = ((PolylineMapObject) object).getPolyline().getVertices();

				for(int j  = 0; j < vertices.length; j++)
				{
					vertices[j] = vertices[j] * this.getWorldToBox() * this.getMapScale();
				}

				ChainShape chain = new ChainShape();
				chain.createChain(vertices);
				groundBody.createFixture(chain, 0f);
				chain.dispose();
			}
			// objects
			else
			{
				this.objectSet.add(object);
//
//				BodyDef groundDef;
//				Body groundBody;
//
//				groundDef = new BodyDef();
//				groundDef.type = BodyDef.BodyType.StaticBody;
//
//
//				groundDef.position.set(Float.parseFloat(object.getProperties().get("x").toString()) * this.getWorldToBox() * this.getMapScale(),
//									   Float.parseFloat(object.getProperties().get("y").toString()) * this.getWorldToBox() * this.getMapScale());
//
//				groundBody = world.createBody(groundDef);
//
//				CircleShape shape = new CircleShape();
//				shape.setRadius(0.2f);
//				shape.setPosition(new Vector2(0.65f, 0.1f));
//				groundBody.createFixture(shape, 0f);
//				shape.dispose();


			}
		}
	}

	/**
	 *
	 * @param spriteBatch
	 */
	public void draw(SpriteBatch spriteBatch, Player player)
	{
		Vector2 playerVector = new Vector2(player.getPosX(), player.getPosY());


		if(this.lastPlayerPos == null)
		{
			this.lastPlayerPos = playerVector;
		}

		if(lastPlayerPos.dst(playerVector) > 200)
		{
			lastPlayerPos = playerVector;

			System.out.print("Recalculating visible objects...");
			int x = 0;
			for(MapObject object : this.objectSet)
			{
				Vector2 objectVector = new Vector2(

					((((Float.parseFloat(object.getProperties().get("x").toString())) * this.getMapScale()) + ((((tiledMap.getTileSets().getTile(Integer.parseInt(object.getProperties().get("gid").toString())).getTextureRegion().getRegionWidth()) * this.getMapScale())) / 2))),
					((((Float.parseFloat(object.getProperties().get("y").toString())) * this.getMapScale()) + ((((tiledMap.getTileSets().getTile(Integer.parseInt(object.getProperties().get("gid").toString())).getTextureRegion().getRegionHeight()) * this.getMapScale())) / 2)))

				);

				if(objectVector.dst(playerVector) < 1400)
				{
					this.visibleObjectSet.add(object);
					x++;
				}
				else
				{
					this.visibleObjectSet.remove(object);
				}
			}
			System.out.println(" " + x);
			x = 0;
		}

		spriteBatch.begin();
		for(MapObject object : this.visibleObjectSet)
		{
				spriteBatch.draw(tiledMap.getTileSets()
										 .getTile(Integer.parseInt(object.getProperties().get("gid").toString()))
										 .getTextureRegion(),
								 Float.parseFloat(object.getProperties().get("x").toString()) * this.getMapScale(),
								 Float.parseFloat(object.getProperties().get("y").toString()) * this.getMapScale(),
								 tiledMap.getTileSets().getTile(Integer.parseInt(object.getProperties()
																					   .get("gid")
																					   .toString())).getTextureRegion().getRegionWidth() *
								 this.getMapScale(),
								 tiledMap.getTileSets().getTile(Integer.parseInt(object.getProperties()
																					   .get("gid")
																					   .toString())).getTextureRegion().getRegionHeight() *
								 this.getMapScale());


		}
		spriteBatch.end();
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
