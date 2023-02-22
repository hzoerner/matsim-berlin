package org.matsim.spnv;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitScheduleFactory;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RunPlanCase1 {

    private static final String CONFIG = "scenarios/berlin-v5.5-10pct/input/berlin-v5.5-10pct.config.xml";
    private static final String OUTPUT = "output/base_case/";

    public static void main(String[] args) {

        Config config = ConfigUtils.loadConfig(CONFIG);
        prepareConfig(config);
        config.controler().setOutputDirectory(OUTPUT);

        Scenario scenario = ScenarioUtils.loadScenario(config);

        Controler controler = new Controler(scenario);
        controler.run();
    }


    private static void prepareConfig(Config config){

        for (long ii = 600; ii <= 97200; ii += 600) {

            for (String act : List.of("business", "educ_higher", "educ_kiga", "educ_other", "educ_primary", "educ_secondary",
                    "educ_tertiary", "errands", "home", "leasure", "shop_daily", "shop_other", "visit", "work")) {
                config.planCalcScore()
                        .addActivityParams(new PlanCalcScoreConfigGroup.ActivityParams(act + "_" + ii).setTypicalDuration(ii));
            }

            config.planCalcScore().addActivityParams(new PlanCalcScoreConfigGroup.ActivityParams("work_" + ii).setTypicalDuration(ii)
                    .setOpeningTime(6. * 3600.).setClosingTime(20. * 3600.));
            config.planCalcScore().addActivityParams(new PlanCalcScoreConfigGroup.ActivityParams("business_" + ii).setTypicalDuration(ii)
                    .setOpeningTime(6. * 3600.).setClosingTime(20. * 3600.));
            config.planCalcScore().addActivityParams(new PlanCalcScoreConfigGroup.ActivityParams("leisure_" + ii).setTypicalDuration(ii)
                    .setOpeningTime(9. * 3600.).setClosingTime(27. * 3600.));
            config.planCalcScore().addActivityParams(new PlanCalcScoreConfigGroup.ActivityParams("shopping_" + ii).setTypicalDuration(ii)
                    .setOpeningTime(8. * 3600.).setClosingTime(20. * 3600.));
        }

        config.plansCalcRoute().setAccessEgressType(PlansCalcRouteConfigGroup.AccessEgressType.accessEgressModeToLink);
        config.qsim().setUsingTravelTimeCheckInTeleportation(true);
        config.qsim().setUsePersonIdForMissingVehicleId(false);
    }

    private static void prepareScenario(Scenario scenario){

        scenario.getNetwork().getLinks().values().stream()
                .forEach(link -> {
                    link.getId();
                });
    }

    private static void prepareTransitSchedule(TransitSchedule schedule){
        var lines = schedule.getTransitLines().values().stream()
                .filter(transitLine -> transitLine.getId().toString().contains("U2"))
                .collect(Collectors.toList());

        for(var line: lines){
            var routes = line.getRoutes().values();

        }

    }

    private static void getNewTransitStops(String shapeFilePath, TransitScheduleFactory factory){

        var featuers = ShapeFileReader.getAllFeatures(shapeFilePath).stream().collect(Collectors.toList());
        Collections.sort(featuers, new FeatureComparator());

        for(var f: featuers){


        }
    }

    private static class FeatureComparator implements Comparator<SimpleFeature>{


        @Override
        public int compare(SimpleFeature o1, SimpleFeature o2) {
            Integer int1 = Integer.parseInt(o1.getAttribute("order").toString());
            Integer int2 = Integer.parseInt(o2.getAttribute("order").toString());

            return int1.compareTo(int2);
        }
    }
}
