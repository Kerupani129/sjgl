package net.kerupani129.sjgltest;

import net.kerupani129.sjgl.SGame;
import net.kerupani129.sjgltest.state.GameMenuState;
import net.kerupani129.sjgltest.state.GameStageState;
import net.kerupani129.sjgltest.state.StartMenuState;

public class TestGame extends SGame {

    //
    // コンストラクタ
    //
    /**
     * ゲームの初期化
     */
	public TestGame() {

		// タイトル設定
		super("SJGL Test");

		// State 設定
        addState(new StartMenuState());
        addState(new GameStageState());
        addState(new GameMenuState());

        enterState(StartMenuState.class);

	}

}