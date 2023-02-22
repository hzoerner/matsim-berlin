package org.matsim.kudamm_2030;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.utils.objectattributes.attributable.Attributes;

import java.util.List;
import java.util.Map;

public class RunPlanCase1 {

    private static final String CONFIG = "scenarios/berlin-v5.5-10pct/input/berlin-v5.5-10pct.config.xml";
    private static final String OUTPUT = "output/plan_case_1/";

    private static final List<Id<Link>> carFreeLinks = List.of(
            Id.createLinkId(""),
            Id.createLinkId(""));

    public static void main(String[] args) {

        Config config = ConfigUtils.loadConfig(CONFIG);
        prepareConfig(config);
        config.controler().setOutputDirectory(OUTPUT);
        config.controler().setRunId("car_free_1");

        Scenario scenario = ScenarioUtils.loadScenario(config);
        prepareScenario(scenario, carFreeLinks);

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

    private static void prepareScenario(Scenario scenario, List<Id<Link>> carfree){

        Network network = scenario.getNetwork();
        Map<Id<Link>, ? extends Link> links = network.getLinks();

        Attributes attributes = network.getAttributes();

        List<String> forbiddenModes = List.of(TransportMode.car, TransportMode.ride);

        for(var id: carfree){
            Link link = links.get(id);
            link.getAllowedModes().removeAll(forbiddenModes);
        }
    }
}
