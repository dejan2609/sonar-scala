/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 - 2013 All contributors
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.buransky.plugins.scoverage.sensor;

import com.buransky.plugins.scoverage.language.Scala;
import com.buransky.plugins.scoverage.language.ScalaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Directory;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

import java.util.HashMap;
import java.util.Map;

public class ScoverageSensor implements Sensor, CoverageExtension {

    private static final Logger LOG = LoggerFactory.getLogger(ScoverageSensor.class);

    public boolean shouldExecuteOnProject(Project project) {
        return project.getAnalysisType().isDynamic(true) && Scala.INSTANCE.getKey().equals(project.getLanguageKey());
    }

    public void analyse(Project project, SensorContext context) {
        parseFakeReport(project, context);
    }

    @Override
    public String toString() {
        return "Scala ScoverageSensor";
    }

    private void parseFakeReport(Project project, final SensorContext context) {
        ProjectFileSystem fileSystem = project.getFileSystem();

        HashMap<String, Directory> dirs = new HashMap<String, Directory>();
        for (InputFile sourceFile : fileSystem.mainFiles("scala")) {
            LOG.info("[ScoverageSensor] Set coverage for [" + sourceFile.getRelativePath() + "]");
            ScalaFile scalaSourcefile = ScalaFile.fromInputFile(sourceFile);

            CoverageMeasuresBuilder coverage = CoverageMeasuresBuilder.create();
            coverage.setHits(1, 1);
            coverage.setHits(2, 2);
            coverage.setHits(3, 3);
            coverage.setHits(4, 0);
            coverage.setHits(5, 0);
            coverage.setHits(6, 0);
            coverage.setHits(7, 0);
            coverage.setHits(8, 1);
            coverage.setHits(9, 0);
            coverage.setHits(10, 2);
            coverage.setHits(11, 0);
            coverage.setHits(12, 3);
            coverage.setHits(13, 0);

            for (Measure measure : coverage.createMeasures()) {
                context.saveMeasure(scalaSourcefile, measure);
            }

            context.saveMeasure(scalaSourcefile, new Measure(CoreMetrics.COVERAGE, 51.4));
            dirs.put(scalaSourcefile.getParent().getKey(), scalaSourcefile.getParent());
        }

        for (Map.Entry<String, Directory> e: dirs.entrySet()) {
            LOG.info("[ScoverageSensor] Set dir coverage for [" + e.getKey() + "]");
            context.saveMeasure(e.getValue(), new Measure(CoreMetrics.COVERAGE, 23.4));
        }

        context.saveMeasure(project, new Measure(CoreMetrics.COVERAGE, 12.3));
    }

}