package entrants.pacman.josh24311;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
    private MOVE myMove = MOVE.NEUTRAL;

    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
    	int current = game.getPacmanCurrentNodeIndex();
        int[] pills = game.getPillIndices();
        int[] powerPills = game.getPowerPillIndices();
        int minDistance = Integer.MAX_VALUE;
        int minDistanceGh = Integer.MAX_VALUE;
        int minDistanceEdGh = Integer.MAX_VALUE;
        int closestPp;
        int closestP = 0;

        int disTonearestPp = 0;
        int disTonearestP = 0;
        int disMinGhAndCloPp = 0;
        int nghWithNp = 0;
        int ghostLocation_now = 2;
        int edghostLocation_now = 2;
        int ghlairtimemux = 1;
        int D1 = 40;
        int D2 = 30;
        boolean ambush_stat = false ;
        Constants.GHOST minGhost = null;
        Constants.GHOST minEdGhost = null;
        // 產生隨機Int 0~4
        Random rn = new Random();
        int randdir = rn.nextInt(5);
        
        for (Constants.GHOST ghost : Constants.GHOST.values())
        {
            // If can't see these will be -1 so all fine there
            // 對於所有的鬼
            if (game.getGhostEdibleTime_new(ghost) == 0 && game.getGhostLairTime_new(ghost) == 0)
            {
                // 如果這支鬼不可食，且不在籠子裡
                int ghostLocation = game.getGhostCurrentNodeIndex_new(ghost);
                int disFromGh = game.getShortestPathDistance(current, ghostLocation);
                if (disFromGh < minDistanceGh)
                {
                    minDistanceGh = disFromGh;
                    minGhost = ghost;
                }
            }
            else if(game.getGhostLairTime(ghost)!=0)
            {
            	ghlairtimemux = ghlairtimemux * game.getGhostLairTime(ghost);
            	//若for 跑完 此值>0表全在籠子內
            }
        }
        //可食鬼找最近
        for (Constants.GHOST ghost : Constants.GHOST.values())
        {
            // If it is > 0 then it is visible so no more PO checks
            //對於所有的鬼
            if (game.getGhostEdibleTime_new(ghost) > 0)
            {
                //如果這支鬼處於可食狀態
                int EdghostLocation = game.getGhostCurrentNodeIndex_new(ghost);
                int disFromEdGh = game.getShortestPathDistance(current, EdghostLocation);
                //取得此鬼跟小精靈最近距離
                if (disFromEdGh < minDistanceEdGh)
                {
                    //做完FOR迴圈之後會找到距離最短的可食鬼
                    minDistanceEdGh = disFromEdGh;
                    minEdGhost = ghost;
                }
            }
        }

        //確認PP是否存在
        ArrayList<Integer> targets = new ArrayList<Integer>();
        for (int i = 0; i < powerPills.length; i++)   // check with power pills
        {
            // are available
            Boolean PowerpillStillAvailable = game.isPowerPillStillAvailable_new(i);
            // 這裡應為誤植，應改為game.isPowerPillStillAvailable(i);
            if (PowerpillStillAvailable != null)
            {
                if (PowerpillStillAvailable)
                {
                    targets.add(powerPills[i]);
                    // 如果這個大力丸存在，則存入target
                }
            }
        }
        //確認P是否存在
        ArrayList<Integer> targets_p = new ArrayList<Integer>();
        for (int i = 0; i < pills.length; i++)   // check with pills are available
        {
            Boolean pillStillAvailable = game.isPillStillAvailable_new(i);
            if (pillStillAvailable != null)
            {
                if (pillStillAvailable)
                {
                    targets_p.add(pills[i]);
                    // 如果這個藥丸存在，則存入target_p
                }
            }
        }
        if(!targets_p.isEmpty())  //還有p
        {
        	//System.out.println("還有p");
            //轉換
            int[] targetsArray_p = new int[targets_p.size()];
            for (int i = 0; i < targetsArray_p.length; i++)
            {
                targetsArray_p[i] = targets_p.get(i);
            }
            closestP = game.getClosestNodeIndexFromNodeIndex(current, targetsArray_p, Constants.DM.PATH);
            disTonearestP = game.getShortestPathDistance(current, closestP);
        }
        if (!targets.isEmpty())   //還有PP存在之狀況
        {
            //轉換
            int[] targetsArray = new int[targets.size()]; // convert from
            // ArrayList to
            // array

            for (int i = 0; i < targetsArray.length; i++)
            {
                targetsArray[i] = targets.get(i);
            }
            closestPp = game.getClosestNodeIndexFromNodeIndex(current, targetsArray, Constants.DM.PATH);
            disTonearestPp = game.getShortestPathDistance(current, closestPp);  
        }
        
        //=========================================判斷式開始
        if(minEdGhost!=null) //有可食鬼
        {
        	edghostLocation_now = game.getGhostCurrentNodeIndex_new(minEdGhost); //取得最近可食鬼的位置
        	if(minGhost!=null) //有一般鬼在籠子外
        	{
        		ghostLocation_now = game.getGhostCurrentNodeIndex_new(minGhost); //取得最近鬼的位置
        		if(minDistanceGh<D2) //一般鬼太近
        		{
        			//System.out.println("Ghost too close MOVE away");
        			//遠離一般鬼
        			return game.getNextMoveAwayFromTarget(current, ghostLocation_now, Constants.DM.PATH);
        		}
        		else   //dis_gh >=D2，一般鬼在一定距離外
        		{
        			if(minDistanceEdGh<D1) // 和可食鬼距離近
        			{
        				//System.out.println("GO HUNTING");
        				//吃可食鬼
        				return game.getNextMoveTowardsTarget(current, edghostLocation_now, Constants.DM.PATH);
        			}
        			else //和可食鬼距離遠
        			{
        				//System.out.println("That edgh too far GO EAT N_P");
        				return game.getNextMoveTowardsTarget(current, closestP, Constants.DM.PATH);
        			}
        		}
        	}
        	else //全部都是可食鬼
        	{
        		if(minDistanceEdGh<D1) // 和可食鬼距離近
    			{
        			//System.out.println("GO HUNTING(all edible)");
    				//吃可食鬼
    				return game.getNextMoveTowardsTarget(current, edghostLocation_now, Constants.DM.PATH);
    			}
    			else //和可食鬼距離遠
    			{
    				//System.out.println("That edgh too far GO EAT N_P");
    				//吃最近小藥丸
    				return game.getNextMoveTowardsTarget(current, closestP, Constants.DM.PATH);
    			}
        	}
        }
        else //無可食鬼
        {
        	if(ghlairtimemux > 1) // 全部一般鬼都在籠子內
        	{
        		//System.out.println("ALL Ghost IN CAGE,GO RANDOM");
        		switch (randdir)//隨機挑一個方向
                {
                case 0:
                    myMove = MOVE.UP;
                    break;
                case 1:
                    myMove = MOVE.RIGHT;
                    break;
                case 2:
                    myMove = MOVE.DOWN;
                    break;
                case 3:
                    myMove = MOVE.LEFT;
                    break;
                case 4:
                    myMove = MOVE.NEUTRAL;
                    break;
                }
        		//回傳此方向
        		return myMove;
        	}
        	else //有一般鬼跑出籠子了
        	{
        		if(minDistanceGh<D2) //一般鬼太近
        		{
        			//System.out.println("Ghost too close MOVE away");
        			//遠離一般鬼
        			return game.getNextMoveAwayFromTarget(current, ghostLocation_now, Constants.DM.PATH);
        		}
        		else//一般鬼在一定距離外
        		{
        			//System.out.println("Ghost far enough EAT N_P");
        			//吃最近小藥丸
        			return game.getNextMoveTowardsTarget(current, closestP, Constants.DM.PATH);
        		}
        	}
        }
    }
}