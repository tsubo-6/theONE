/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import java.util.List;

import core.Coord;
import core.Settings;
import movement.map.DijkstraPathFinder;
import movement.map.MapNode;
import movement.map.PointsOfInterest;

/**
 * Map based movement model that uses Dijkstra's algorithm to find shortest
 * paths between two random map nodes and Points Of Interest
 */
public class ShortestPathMapBasedMovement extends MapBasedMovement implements 
	SwitchableMovement {
	/** the Dijkstra shortest path finder */
	private DijkstraPathFinder pathFinder;

	/** Points Of Interest handler */
	private PointsOfInterest pois;
				
	/**
	 * Creates a new movement model based on a Settings object's settings.
	 * @param settings The Settings object where the settings are read from
	 */
	public ShortestPathMapBasedMovement(Settings settings) {
		super(settings);
		this.pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
		this.pois = new PointsOfInterest(getMap(), getOkMapNodeTypes(),
				settings, rng);
	}
	
	/**
	 * Copyconstructor.
	 * @param mbm The ShortestPathMapBasedMovement prototype to base 
	 * the new object to 
	 */
	protected ShortestPathMapBasedMovement(ShortestPathMapBasedMovement mbm) {
		super(mbm);
		this.pathFinder = mbm.pathFinder;
		this.pois = mbm.pois;
	}
	
	@Override
	public Path getPath() {
		Path p = new Path(generateSpeed());
		// selectDestination() : POIグループあるいはマップノードから目的地選択する関数 (PointOfInterestクラス)
		MapNode to = pois.selectDestination();
		List<MapNode> nodePath = pathFinder.getShortestPath(lastMapNode, to);

		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to " +
			to + ". The simulation map isn't fully connected";
				
		for (MapNode node : nodePath) { // create a Path from the shortest path
			// Coord型のリストに目的地までのパスを格納
			p.addWaypoint(node.getLocation());
		}
		
		lastMapNode = to;
		
		return p;
	}	
	
	//近大周辺第一次避難所poiからパス生成
	public Path getPath(int num,Coord coord) {
		Path p = new Path(generateSpeed());
		MapNode to = pois.forceSelectPoi(num);
		MapNode now = map.getNodeByCoord(coord);
		//System.out.println("現在位置のデータ(MapNode):"+now);
		//System.out.println("forceSelectPoiの目的地データ:"+to);

		List<MapNode> nodePath = pathFinder.getShortestPath(now, to);
		//System.out.println(nodePath);

		for (MapNode node : nodePath) { // create a Path from the shortest path
			// Coord型のリストに目的地までのパスを格納
			p.addWaypoint(node.getLocation());
		}
		
		lastMapNode = now;
		
		return p;
	}
	
	@Override
	public ShortestPathMapBasedMovement replicate() {
		return new ShortestPathMapBasedMovement(this);
	}
	
	public Path pauseMovement() {
		return new Path(0);
    }

}
