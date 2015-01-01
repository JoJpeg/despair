package de.lfstudios.game.controller;

import de.lfstudios.game.Despair;
import de.lfstudios.game.model.GameModel;
import de.lfstudios.game.view.GameScreen;

/**
 * @author vwiebe
 */
public class GameController
{
	private Despair game;
	private GameModel gameModel;
	private GameScreen gameScreen;

	/**
	 *
	 * @param game
	 */
	public GameController(Despair game)
	{
		this.game = game;
		this.gameModel = new GameModel();
		this.gameScreen = new GameScreen(this.game);
	}

	/**
	 *
	 * @return
	 */
	public GameModel getGameModel()
	{
		return gameModel;
	}

	/**
	 *
	 * @param gameModel
	 */
	public void setGameModel(GameModel gameModel)
	{
		this.gameModel = gameModel;
	}

	/**
	 *
	 * @return
	 */
	public GameScreen getGameScreen()
	{
		return gameScreen;
	}

	/**
	 *
	 * @param gameScreen
	 */
	public void setGameScreen(GameScreen gameScreen)
	{
		this.gameScreen = gameScreen;
	}
}
