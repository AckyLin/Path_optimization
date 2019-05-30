/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.jsprit.examples;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;

import java.io.File;
import java.util.Collection;


public class SimpleExample {


    public static void main(String[] args) {
        /*
         * 一些准备 - 创建输出文件夹
		 */
        File dir = new File("output");
        // 如果目录不存在，请创建它
        if (!dir.exists()) {
            System.out.println("creating directory ./output");
            boolean result = dir.mkdir();
            if (result) System.out.println("./output created");
        }

		/*
         * 获得一个车辆类型构建器并构建一个类型为“vehicleType”的类型和一个容量维度，即权重，容量维度值为2
		 */
        final int WEIGHT_INDEX = 0;
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(WEIGHT_INDEX, 8);
        VehicleType vehicleType = vehicleTypeBuilder.build();

		/*
         * 得到一个车辆制造商并建造一个位于（10,10）的车辆，型号为“vehicleType”
		 */
        Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(Location.newInstance(10, 10));
        vehicleBuilder.setType(vehicleType);
        VehicleImpl vehicle = vehicleBuilder.build();

		/*
         * 在所需位置构建服务，每个服务的容量需求为1。
		 */
        Service service1 = Service.Builder.newInstance("1").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(5, 7)).build();
        Service service2 = Service.Builder.newInstance("2").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(7, 15)).build();

        Service service3 = Service.Builder.newInstance("3").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(15, 7)).build();
        Service service4 = Service.Builder.newInstance("4").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(15, 13)).build();

        Service service5 = Service.Builder.newInstance("5").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(20, 7)).build();
        Service service6 = Service.Builder.newInstance("6").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(21, 10)).build();
        Service service7 = Service.Builder.newInstance("7").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(15, 8)).build();
        Service service8 = Service.Builder.newInstance("8").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(19, 2)).build();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);
        vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4).addJob(service5).addJob(service6).addJob(service7).addJob(service8);

        VehicleRoutingProblem problem = vrpBuilder.build();

		/*
         * 得到开箱即用的算法。
		 */
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

		/*
         * 并搜索解决方案
		 */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		/*
         * 得到最好的
		 */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

		/*
         * 策划
		 */
        new Plotter(problem,bestSolution).plot("output/plot.png","simple example");

        /*
        使用GraphStream渲染问题和解决方案
         */
        new GraphStreamViewer(problem, bestSolution).labelWith(Label.ID).setRenderDelay(100).display();
    }

}
