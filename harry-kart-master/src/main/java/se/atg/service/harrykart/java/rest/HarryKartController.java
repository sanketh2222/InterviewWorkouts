package se.atg.service.harrykart.java.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.atg.service.harrykart.java.rest.dto.FinalRanking;
import se.atg.service.harrykart.java.rest.dto.HarryKart;
import se.atg.service.harrykart.java.rest.dto.Lane;
import se.atg.service.harrykart.java.rest.dto.Loop;

@RestController
@RequestMapping("/java/api")
public class HarryKartController {
	
	final int distance=1000;

    @PostMapping(path = "/play", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map playHarryKart(@RequestBody HarryKart raceinfo) {
    	List<FinalRanking> timeCalculatedList=new ArrayList<>();
    	Map rankingDetails=new HashMap<String, List<FinalRanking>>(); 
    	Map<Double, FinalRanking> horseRanking=new TreeMap<>();
    	
    	raceinfo.getParticipantList().forEach(p-> {
    		double timetakenforhorse=calculateTimeBySpeed(distance, p.getBaseSpeed());
    		int totalPowerUpPoint = 0;
    		
    		for(Loop loop:raceinfo.getPowerUps()) {
    			
    			for(Lane lane:loop.getLane())
    			{   				
    			if(lane.getLaneNumber()==p.getLane()) {
    				totalPowerUpPoint+=lane.getPowerUpPoint();
    				timetakenforhorse+=calculateTimeBySpeed(distance, p.getBaseSpeed()+totalPowerUpPoint);
    				break;
    			}					
    			}
    			
    		}  	
    		
    		FinalRanking timetakenbyHorse=new FinalRanking();
    		timetakenbyHorse.setHorse(p.getName());
    		timetakenbyHorse.setTotaltimetaken(timetakenforhorse);
    		horseRanking.put(timetakenforhorse, timetakenbyHorse);
    		//timeCalculatedList.add(timetakenbyHorse);
    		
    	});
    	final List<FinalRanking> rankingList=horseRanking.values().stream().limit(3).collect(Collectors.toList());
		/*
		 * int i=0; horseRanking.entrySet().forEach(e->{
		 * 
		 * e.getValue().setPosition(i++); });
		 */
    //	final List<FinalRanking> rankingList=timeCalculatedList.stream().sorted((o1, o2) -> Double.compare(o1.getTotaltimetaken(), o2.getTotaltimetaken())).limit(3).collect(Collectors.toList());
		
		  IntStream.range(0, rankingList.size()).forEach(index ->
		  rankingList.get(index).setPosition(index+1));
		 
    	rankingDetails.put("ranking", rankingList);
    	return rankingDetails;
    }
    
    public double calculateTimeBySpeed(int distance,int speed) {
		double timetaken = 0;
		if(speed>0) {
		timetaken=distance/speed;
		}
		return timetaken;	
	}

}
