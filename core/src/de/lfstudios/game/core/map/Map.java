package de.lfstudios.game.core.map;

import com.badlogic.gdx.Gdx;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

//	private HashSet<MapObject>[][] chunkSet = new HashSet[32][32];

	private HashSet<MapObject> objectSet = new HashSet<MapObject>();

	private ArrayList<MapObject> backgroundObjectSet = new ArrayList<MapObject>();
	private ArrayList<MapObject> visibleObjectSet = new ArrayList<MapObject>();

	private Vector2 lastCameraPos;


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

//		this.generateChunks();
	}

//	private void generateChunks()
//	{
//		int i, j;
//
//		for(i = 0; i < 32; i++)
//		{
//			for(j = 0; j < 32; j++)
//			{
//				this.chunkSet[i][j] = new HashSet<MapObject>();
//			}
//		}
//
//		for(MapObject object : this.objectSet)
//		{
//			int chunkX = (int) (Float.parseFloat(object.getProperties().get("x").toString()) / 128);
//			int chunkY = (int) (Float.parseFloat(object.getProperties().get("y").toString()) / 128);
//
//			this.chunkSet[chunkX][chunkY].add(object);
//		}
//	}

	/**
	 *
	 * @param cameraPosition
	 */
	private HashSet<MapObject> getCurrentObjectSet(Vector2 cameraPosition)
	{
		return this.objectSet;
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
	public void draw(SpriteBatch spriteBatch, Player player, Vector2 cameraPosition)
	{
		if(this.lastCameraPos == null)
		{
			this.lastCameraPos = cameraPosition;
			calculateVisibleObjects(cameraPosition);
		}

		if(lastCameraPos.dst(cameraPosition) > 200)
		{
			lastCameraPos = cameraPosition;
			this.calculateVisibleObjects(cameraPosition);
		}

		// draw background objects
		for(MapObject object : this.backgroundObjectSet)
		{
			spriteBatch.begin();
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
			spriteBatch.end();
		}

		// draw everything else
		boolean playerIsDrawn = false;
		for(MapObject object : this.visibleObjectSet)
		{
			float objOrigin = Float.parseFloat(object.getProperties().get("y").toString()) * this.getMapScale();

			if(object.getProperties().get("originOffset") != null)
			{
				objOrigin = objOrigin + Float.parseFloat(object.getProperties().get("originOffset").toString()) * this.getMapScale();
			}

			if(player.getPosY() > objOrigin && !playerIsDrawn)
			{
				player.draw(spriteBatch);
				playerIsDrawn = true;
			}

			spriteBatch.begin();
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
			spriteBatch.end();
		}
	}

	/**
	 *
	 * @param cameraPosition
	 */
	private void calculateVisibleObjects(Vector2 cameraPosition)
	{
		this.visibleObjectSet.clear();
		System.out.print("Recalculating visible objects...");
		for (MapObject object : this.getCurrentObjectSet(cameraPosition)) // TODO: HEAVYY!!!!!!!!!!
		{
			float objX = Float.parseFloat(object.getProperties().get("x").toString()) * this.getMapScale();
			float objY = Float.parseFloat(object.getProperties().get("y").toString()) * this.getMapScale();
			float objWidth = (tiledMap.getTileSets().getTile(Integer.parseInt(object.getProperties().get("gid").toString())).getTextureRegion().getRegionWidth()) * this.getMapScale();
			float objHeight = (tiledMap.getTileSets().getTile(Integer.parseInt(object.getProperties().get("gid").toString())).getTextureRegion().getRegionHeight()) * this.getMapScale();

			float left = cameraPosition.x - (Gdx.graphics.getWidth() / 2);
			float top = cameraPosition.y + (Gdx.graphics.getHeight() / 2);
			float right = cameraPosition.x + (Gdx.graphics.getWidth() / 2);
			float bottom = cameraPosition.y - (Gdx.graphics.getHeight() / 2);


			if(object.getProperties().get("isBackground") != null)
			{
				// background objects
				if (((objX + objWidth) > left && objX < right) && (objY < top && (objY + objHeight) > bottom))
				{
					if (!this.backgroundObjectSet.contains(object))
					{
						this.backgroundObjectSet.add(object);
					}
				}
				else
				{
					this.backgroundObjectSet.remove(object);
				}
			}
			else
			{
				// non background objects
				if (((objX + objWidth) > left && objX < right) && (objY < top && (objY + objHeight) > bottom))
				{
					if (!this.visibleObjectSet.contains(object))
					{
						this.visibleObjectSet.add(object);
					}
				}
				else
				{
					this.visibleObjectSet.remove(object);
				}

			}
		}
		System.out.println(this.visibleObjectSet.size());

		Collections.sort(this.visibleObjectSet, new Comparator<MapObject>()
		{
			@Override
			public int compare(MapObject o1, MapObject o2)
			{
				float o1Origin = Float.parseFloat(o1.getProperties().get("y").toString());
				float o2Origin = Float.parseFloat(o2.getProperties().get("y").toString());

				if(o1.getProperties().get("originOffset") != null)
				{
					o1Origin = o1Origin +
							   Float.parseFloat(o1.getProperties()
												  .get("originOffset")
												  .toString());
				}

				if(o2.getProperties().get("originOffset") != null)
				{
					o2Origin = o2Origin +
							   Float.parseFloat(o2.getProperties()
												  .get("originOffset")
												  .toString());
				}

				if (o1Origin < o2Origin)
				{
					return 1;
				}
				else if (o1Origin > o2Origin)
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}
		});
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
