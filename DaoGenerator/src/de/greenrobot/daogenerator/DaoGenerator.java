/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daogenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * Once you have your model created, use this class to generate entities and DAOs.
 * 
 * @author Markus
 */
public class DaoGenerator {

    private Pattern patternKeepIncludes;
    private Pattern patternKeepFields;
    private Pattern patternKeepMethods;
    private Pattern patternKeepBody;

    private Template templateDao;
    private Template templateDaoMaster;
    private Template templateDaoSession;
    private Template templateEntity;
    private Template templateDaoUnitTest;
    private Template templateContentProvider;
    private Template templateEntityEvent;
    private Template templateEntityListEvent;
    private Template templateEntityListViewBase;
    private Template templateEntityListViewDefault;
    private Template templateEntityListViewDefaultXml;
    private Template templateEntityItemView;
    private Template templateEntityItemViewXml;
    private Template templateEntityAdapter;
    private Template templateEntityCursorAdapter;
    private Template templateGeneratedViewComponent;
    private Template templateDaoModule;

    public DaoGenerator() throws IOException {
        System.out.println("greenDAO Generator");
        System.out.println("Copyright 2011-2013 Markus Junginger, greenrobot.de. Licensed under GPL V3.");
        System.out.println("This program comes with ABSOLUTELY NO WARRANTY");

        patternKeepIncludes = compilePattern("INCLUDES");
        patternKeepFields = compilePattern("FIELDS");
        patternKeepMethods = compilePattern("METHODS");
        patternKeepBody = compilePattern("BODY");

        Configuration config = new Configuration();
        config.setClassForTemplateLoading(this.getClass(), "/");
        config.setObjectWrapper(new DefaultObjectWrapper());

        templateDao = config.getTemplate("dao.ftl");
        templateDaoMaster = config.getTemplate("dao-master.ftl");
        templateDaoSession = config.getTemplate("dao-session.ftl");
        templateEntity = config.getTemplate("entity.ftl");
        templateDaoUnitTest = config.getTemplate("dao-unit-test.ftl");
        templateContentProvider = config.getTemplate("content-provider.ftl");
        templateEntityEvent = config.getTemplate("entity-event.ftl");
        templateEntityListEvent = config.getTemplate("entity-list-event.ftl");

        templateEntityListViewBase = config.getTemplate("entity-list-view-base.ftl");
        templateEntityListViewDefault = config.getTemplate("entity-list-view-default.ftl");
        templateEntityListViewDefaultXml = config.getTemplate("entity-list-view-default-xml.ftl");
        templateEntityItemView = config.getTemplate("entity-item-view.ftl");
        templateEntityItemViewXml = config.getTemplate("entity-item-view-xml.ftl");
        templateEntityAdapter = config.getTemplate("entity-adapter.ftl");
        templateEntityCursorAdapter = config.getTemplate("entity-cursor-adapter.ftl");
        templateGeneratedViewComponent = config.getTemplate("entity-view-component.ftl");
        templateDaoModule = config.getTemplate("dao-module.ftl");
    }

    private Pattern compilePattern(String sectionName) {
        int flags = Pattern.DOTALL | Pattern.MULTILINE;
        return Pattern.compile(".*^\\s*?//\\s*?KEEP " + sectionName + ".*?\n(.*?)^\\s*// KEEP " + sectionName
                + " END.*?\n", flags);
    }
//
//    /** Generates all entities and DAOs for the given schema. */
//    public void generateAll(Schema schema, String outDir, String outDirTest) throws Exception {
//        generateAll(schema, outDir, outDirTest);
//    }

    /** Generates all entities and DAOs for the given schema. */
    public void generateAll(Schema schema, String outDir, String outDirRes) throws Exception {
        long start = System.currentTimeMillis();

        File outDirFile = toFileForceExists(outDir);

        File outDirTestFile = null;

        File outDirResFile = toFileForceExists(outDirRes);

        schema.init2ndPass();
        schema.init3ndPass();

        System.out.println("Processing schema version " + schema.getVersion() + "...");

        List<Entity> entities = schema.getEntities();
        for (Entity entity : entities) {
            generate(templateDao, outDirFile, entity.getJavaPackageDao(), entity.getClassNameDao(), schema, entity);
            if (!entity.isProtobuf() && !entity.isSkipGeneration()) {
                generate(templateEntity, outDirFile, entity.getJavaPackage(), entity.getClassName(), schema, entity);
                if (schema.isEventGenerated() && !entity.isSkipGenerationEvent()) {
                    generate(templateEntityEvent, outDirFile, schema.getDefaultJavaPackageEvent(), entity.getClassName() + "Event", schema, entity);
                    generate(templateEntityListEvent, outDirFile, schema.getDefaultJavaPackageEvent(), entity.getClassName() + "ListEvent", schema, entity);

                    if (entity.isGenerateListView()) {
                        generate(templateEntityListViewBase, outDirFile, schema.getDefaultJavaPackage() +".view.base", "Base" + entity.getClassName() + "ListView", schema, entity);
                        generate(templateEntityListViewDefault, outDirFile, schema.getDefaultJavaPackage() +".view", "Default" + entity.getClassName() + "ListView", schema, entity);
                        generate(templateEntityAdapter, outDirFile, schema.getDefaultJavaPackage() +".view.adapter", entity.getClassName() + "ListAdapter", schema, entity);
                        generate(templateEntityCursorAdapter, outDirFile, schema.getDefaultJavaPackage() +".view.adapter", entity.getClassName() + "CursorAdapter", schema, entity);
                        generate(templateEntityItemView, outDirFile, schema.getDefaultJavaPackage() +".view.item", entity.getClassName() + "ListItemView", schema, entity);
                        generate(templateEntityItemViewXml, outDirResFile, "layout", entity.getTableName().toLowerCase() + "_list_item_view", schema, entity, "xml");
                        generate(templateEntityListViewDefaultXml, outDirResFile, "layout", entity.getTableName().toLowerCase() + "_list_view_default", schema, entity, "xml");
                    }
                }

            }
            if (outDirTestFile != null && !entity.isSkipGenerationTest()) {
                String javaPackageTest = entity.getJavaPackageTest();
                String classNameTest = entity.getClassNameTest();
                File javaFilename = toJavaFilename(outDirTestFile, javaPackageTest, classNameTest, "java");
                if (!javaFilename.exists()) {
                    generate(templateDaoUnitTest, outDirTestFile, javaPackageTest, classNameTest, schema, entity);
                } else {
                    System.out.println("Skipped " + javaFilename.getCanonicalPath());
                }
            }
            for (ContentProvider contentProvider : entity.getContentProviders()) {
                Map<String, Object> additionalObjectsForTemplate = new HashMap<String, Object>();
                additionalObjectsForTemplate.put("contentProvider", contentProvider);
                generate(templateContentProvider, outDirFile, entity.getJavaPackage(), entity.getClassName()
                        + "ContentProvider", schema, entity, additionalObjectsForTemplate);
            }
        }
        generate(templateDaoMaster, outDirFile, schema.getDefaultJavaPackageDao(), "DaoMaster", schema, null);
        generate(templateDaoSession, outDirFile, schema.getDefaultJavaPackageDao(), "DaoSession", schema, null);
        generate(templateGeneratedViewComponent, outDirFile, schema.getDefaultJavaPackage() + ".dagger.component", "EntityViewComponent", schema, null);
        generate(templateDaoModule, outDirFile, schema.getDefaultJavaPackage() + ".dagger.module", "DaoModule", schema, null);

        long time = System.currentTimeMillis() - start;
        System.out.println("Processed " + entities.size() + " entities in " + time + "ms");
    }

    protected File toFileForceExists(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException(filename
                    + " does not exist. This check is to prevent accidental file generation into a wrong path.");
        }
        return file;
    }

    private void generate(Template template, File outDirFile, String javaPackage, String javaClassName, Schema schema,
                          Entity entity) throws Exception {
        generate(template, outDirFile, javaPackage, javaClassName, schema, entity, null, "java");
    }
    private void generate(Template template, File outDirFile, String javaPackage, String javaClassName, Schema schema,
            Entity entity, String fileSuffix) throws Exception {
        generate(template, outDirFile, javaPackage, javaClassName, schema, entity, null, fileSuffix);
    }
    private void generate(Template template, File outDirFile, String javaPackage, String javaClassName, Schema schema,
                          Entity entity, Map<String, Object> additionalObjectsForTemplate) throws Exception {
        generate(template, outDirFile, javaPackage, javaClassName, schema, entity, additionalObjectsForTemplate, "java");
    }

    private void generate(Template template, File outDirFile, String javaPackage, String javaClassName, Schema schema,
            Entity entity, Map<String, Object> additionalObjectsForTemplate, String fileSuffix) throws Exception {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("schema", schema);
        root.put("entity", entity);
        if (additionalObjectsForTemplate != null) {
            root.putAll(additionalObjectsForTemplate);
        }
        try {
            File file = toJavaFilename(outDirFile, javaPackage, javaClassName, fileSuffix);
            if (file.exists() && fileSuffix.equals("xml")) {
                //Don't overwrite the resources generated as they contain no data
                return;
            }
            file.getParentFile().mkdirs();

            if (entity != null && entity.getHasKeepSections()) {
                checkKeepSections(file, root);
            }

            Writer writer = new FileWriter(file);
            try {
                template.process(root, writer);
                writer.flush();
                System.out.println("Written " + file.getCanonicalPath());
            } finally {
                writer.close();
            }
        } catch (Exception ex) {
            System.err.println("Data map for template: " + root);
            System.err.println("Error while generating " + javaPackage + "." + javaClassName + " ("
                    + outDirFile.getCanonicalPath() + ")");
            throw ex;
        }
    }

    private void checkKeepSections(File file, Map<String, Object> root) {
        if (file.exists()) {
            try {
                String contents = new String(DaoUtil.readAllBytes(file));

                Matcher matcher;

                matcher = patternKeepIncludes.matcher(contents);
                if (matcher.matches()) {
                    root.put("keepIncludes", matcher.group(1));
                }

                matcher = patternKeepFields.matcher(contents);
                if (matcher.matches()) {
                    root.put("keepFields", matcher.group(1));
                }

                matcher = patternKeepMethods.matcher(contents);
                if (matcher.matches()) {
                    root.put("keepMethods", matcher.group(1));
                }

                matcher = patternKeepBody.matcher(contents);
                if (matcher.matches()) {
                    root.put("keepBody", matcher.group(1));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected File toJavaFilename(File outDirFile, String javaPackage, String javaClassName, String fileSuffix) {
        String packageSubPath = javaPackage.replace('.', '/');
        File packagePath = new File(outDirFile, packageSubPath);
        File file = new File(packagePath, javaClassName + "." + fileSuffix);
        return file;
    }

}
