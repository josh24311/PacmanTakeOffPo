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
        // �����H��Int 0~4
        Random rn = new Random();
        int randdir = rn.nextInt(5);
        
        for (Constants.GHOST ghost : Constants.GHOST.values())
        {
            // If can't see these will be -1 so all fine there
            // ���Ҧ�����
            if (game.getGhostEdibleTime_new(ghost) == 0 && game.getGhostLairTime_new(ghost) == 0)
            {
                // �p�G�o�䰭���i���A�B���bŢ�l��
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
            	//�Yfor �]�� ����>0����bŢ�l��
            }
        }
        //�i������̪�
        for (Constants.GHOST ghost : Constants.GHOST.values())
        {
            // If it is > 0 then it is visible so no more PO checks
            //���Ҧ�����
            if (game.getGhostEdibleTime_new(ghost) > 0)
            {
                //�p�G�o�䰭�B��i�����A
                int EdghostLocation = game.getGhostCurrentNodeIndex_new(ghost);
                int disFromEdGh = game.getShortestPathDistance(current, EdghostLocation);
                //���o������p���F�̪�Z��
                if (disFromEdGh < minDistanceEdGh)
                {
                    //����FOR�j�餧��|���Z���̵u���i����
                    minDistanceEdGh = disFromEdGh;
                    minEdGhost = ghost;
                }
            }
        }

        //�T�{PP�O�_�s�b
        ArrayList<Integer> targets = new ArrayList<Integer>();
        for (int i = 0; i < powerPills.length; i++)   // check with power pills
        {
            // are available
            Boolean PowerpillStillAvailable = game.isPowerPillStillAvailable_new(i);
            // �o�������~�ӡA���אּgame.isPowerPillStillAvailable(i);
            if (PowerpillStillAvailable != null)
            {
                if (PowerpillStillAvailable)
                {
                    targets.add(powerPills[i]);
                    // �p�G�o�Ӥj�O�Y�s�b�A�h�s�Jtarget
                }
            }
        }
        //�T�{P�O�_�s�b
        ArrayList<Integer> targets_p = new ArrayList<Integer>();
        for (int i = 0; i < pills.length; i++)   // check with pills are available
        {
            Boolean pillStillAvailable = game.isPillStillAvailable_new(i);
            if (pillStillAvailable != null)
            {
                if (pillStillAvailable)
                {
                    targets_p.add(pills[i]);
                    // �p�G�o���ĤY�s�b�A�h�s�Jtarget_p
                }
            }
        }
        if(!targets_p.isEmpty())  //�٦�p
        {
        	//System.out.println("�٦�p");
            //�ഫ
            int[] targetsArray_p = new int[targets_p.size()];
            for (int i = 0; i < targetsArray_p.length; i++)
            {
                targetsArray_p[i] = targets_p.get(i);
            }
            closestP = game.getClosestNodeIndexFromNodeIndex(current, targetsArray_p, Constants.DM.PATH);
            disTonearestP = game.getShortestPathDistance(current, closestP);
        }
        if (!targets.isEmpty())   //�٦�PP�s�b�����p
        {
            //�ഫ
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
        
        //=========================================�P�_���}�l
        if(minEdGhost!=null) //���i����
        {
        	edghostLocation_now = game.getGhostCurrentNodeIndex_new(minEdGhost); //���o�̪�i��������m
        	if(minGhost!=null) //���@�밭�bŢ�l�~
        	{
        		ghostLocation_now = game.getGhostCurrentNodeIndex_new(minGhost); //���o�̪񰭪���m
        		if(minDistanceGh<D2) //�@�밭�Ӫ�
        		{
        			//System.out.println("Ghost too close MOVE away");
        			//�����@�밭
        			return game.getNextMoveAwayFromTarget(current, ghostLocation_now, Constants.DM.PATH);
        		}
        		else   //dis_gh >=D2�A�@�밭�b�@�w�Z���~
        		{
        			if(minDistanceEdGh<D1) // �M�i�����Z����
        			{
        				//System.out.println("GO HUNTING");
        				//�Y�i����
        				return game.getNextMoveTowardsTarget(current, edghostLocation_now, Constants.DM.PATH);
        			}
        			else //�M�i�����Z����
        			{
        				//System.out.println("That edgh too far GO EAT N_P");
        				return game.getNextMoveTowardsTarget(current, closestP, Constants.DM.PATH);
        			}
        		}
        	}
        	else //�������O�i����
        	{
        		if(minDistanceEdGh<D1) // �M�i�����Z����
    			{
        			//System.out.println("GO HUNTING(all edible)");
    				//�Y�i����
    				return game.getNextMoveTowardsTarget(current, edghostLocation_now, Constants.DM.PATH);
    			}
    			else //�M�i�����Z����
    			{
    				//System.out.println("That edgh too far GO EAT N_P");
    				//�Y�̪�p�ĤY
    				return game.getNextMoveTowardsTarget(current, closestP, Constants.DM.PATH);
    			}
        	}
        }
        else //�L�i����
        {
        	if(ghlairtimemux > 1) // �����@�밭���bŢ�l��
        	{
        		//System.out.println("ALL Ghost IN CAGE,GO RANDOM");
        		switch (randdir)//�H���D�@�Ӥ�V
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
        		//�^�Ǧ���V
        		return myMove;
        	}
        	else //���@�밭�]�XŢ�l�F
        	{
        		if(minDistanceGh<D2) //�@�밭�Ӫ�
        		{
        			//System.out.println("Ghost too close MOVE away");
        			//�����@�밭
        			return game.getNextMoveAwayFromTarget(current, ghostLocation_now, Constants.DM.PATH);
        		}
        		else//�@�밭�b�@�w�Z���~
        		{
        			//System.out.println("Ghost far enough EAT N_P");
        			//�Y�̪�p�ĤY
        			return game.getNextMoveTowardsTarget(current, closestP, Constants.DM.PATH);
        		}
        	}
        }
    }
}