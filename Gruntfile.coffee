"use strict"

module.exports = (grunt) ->
  require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks)

  appConfig =
    pkg : grunt.file.readJSON("package.json")
    filename: "<%= config.pkg.name %>"
    version: "<%= config.pkg.version %>"
    filenameversion: "<%= config.filename %>-<%= config.version %>"
    dir:
      app: "app"
      resources:
        root: "resources"
        less: "<%= config.dir.resources.root %>/less"
        coffee: "<%= config.dir.resources.root %>/coffee"
        images: "<%= config.dir.resources.root %>/images"
        fonts: "<%= config.dir.resources.root %>/fonts"
        components: "<%= config.dir.resources.root %>/components"
      dist:
        root: "public"
        images: "<%= config.dir.dist.root %>/images"
        styles: "<%= config.dir.dist.root %>/stylesheets"
        scripts: "<%= config.dir.dist.root %>/javascripts"
        vendors: "<%= config.dir.dist.scripts %>/vendors"
      tmp:
        root: "tmp"
        coffee: "<%= config.dir.tmp.root %>/coffee"
        scripts: "<%= config.dir.tmp.root %>/scripts"

  grunt.renameTask('regarde', 'watch')

  grunt.registerTask("default", ["build", "livereload-start", "watch"])

  grunt.registerTask("build", ["coffeeBuild", "lessBuild", "componentsBuild", "imagesBuild", "fontsBuild"])
  grunt.registerTask("dist", ["coffeeDist", "lessDist", "componentsBuild", "imagesBuild", "fontsBuild", "concat:postDist", "uglify"]) # , "jshint"

  grunt.registerTask("coffeeBuild", ["clean:tmpCoffee", "clean:scripts", "coffee:raw"])
  grunt.registerTask("coffeeDist", ["clean:tmpCoffee", "clean:tmpScripts", "clean:scripts", "coffee:dist"])

  grunt.registerTask("lessBuild", ["clean:styles", "recess:raw"])
  grunt.registerTask("lessDist", ["clean:styles", "recess"])

  grunt.registerTask("imagesBuild", ["copy:images"])
  grunt.registerTask("fontsBuild", ["copy:fonts"])
  grunt.registerTask("componentsBuild", ["copy:components"])

  grunt.initConfig
    config: appConfig

    meta:
      banner : '/*! <%= config.pkg.title || config.pkg.name %> - v<%= config.pkg.version %> - ' +
          '<%= grunt.template.today("yyyy-mm-dd") %>\n' + '<%= config.pkg.homepage ? "* " + config.pkg.homepage + "\n" : "" %>' +
          '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= config.config.pkg.author.name %>;' +
          ' Licensed <%= _.pluck(config.pkg.licenses, "type").join(", ") %> */'

    clean:
      tmpCoffee: ["<%= config.dir.tmp.coffee %>"]
      tmpScripts: ["<%= config.dir.tmp.scripts %>"]
      styles: ["<%= config.dir.dist.styles %>"]
      scripts: ["<%= config.dir.dist.scripts %>"]
      images: ["<%= config.dir.dist.images %>"]

    concat:
      postDist:
        files:
          "<%= config.dir.dist.scripts %>/main-<%= config.version %>.js": [
            "<%= config.dir.resources.components %>/jquery/jquery.js",
            "<%= config.dir.resources.components %>/lodash/lodash.js",
            "<%= config.dir.tmp.scripts %>/main-<%= config.version %>.js"
          ]

    copy:
      components:
        expand: true
        cwd: "<%= config.dir.resources.components %>"
        dest: "<%= config.dir.dist.scripts %>/vendors"
        src: ["*/*.js"]
      fonts:
        expand: true
        dot: true
        cwd: "<%= config.dir.resources.fonts %>"
        dest: "<%= config.dir.dist.styles %>/fonts"
        src: ["**/*"]
      images:
        expand: true
        dot: true
        cwd: "<%= config.dir.resources.images %>"
        dest: "<%= config.dir.dist.images %>"
        src: ["*"]

    coffee:
      raw:
        expand: true
        cwd: "<%= config.dir.resources.coffee %>"
        src: ["**/*.coffee"]
        dest: "<%= config.dir.dist.scripts %>"
        ext: ".js"
        options:
          bare: true
      dist:
        options:
          join: true
          bare: true
        files:
          "<%= config.dir.dist.scripts %>/require-main-<%= config.version %>.js": ["<%= config.dir.resources.coffee %>/require-main.coffee"]
          "<%= config.dir.tmp.scripts %>/main-<%= config.version %>.js": ["<%= config.dir.resources.coffee %>/main.coffee", "<%= config.dir.resources.coffee %>/**/*.coffee", "!<%= config.dir.resources.coffee %>/require-main.coffee"]

    recess:
      raw:
        options:
          compile:true
        files:
          "<%= config.dir.dist.styles %>/main-<%= config.version %>.css": ["<%= config.dir.resources.less %>/main.less"]
      min:
        options:
          compile:true
          compress: true
        files:
          "<%= config.dir.dist.styles %>/main-<%= config.version %>.min.css": ["<%= config.dir.dist.styles %>/main-<%= config.version %>.css"]

    uglify:
      js:
        files:
          "<%= config.dir.dist.scripts %>/require-<%= config.version %>.min.js": [ "<%= config.dir.dist.scripts %>/require-<%= config.version %>.js" ]
          "<%= config.dir.dist.scripts %>/main-<%= config.version %>.min.js": [ "<%= config.dir.dist.scripts %>/main-<%= config.version %>.js" ]

    jshint:
      options:
        jshintrc: ".jshintrc"
      all: ["<%= config.dir.dist.scripts %>/main-<%= config.version %>.js"]

    requirejs:
      dist:
        options:
          baseUrl: "public/javascripts"
          optimize: "none"
          preserveLicenseComments: false
          useStrict: true
          wrap: true

    watch:
      gruntfile:
        files: ["Gruntfile.coffee"]
        tasks: ["build"]
      coffee:
        files: ["resources/coffee/**/*"]
        tasks: ["coffeeBuild"]
      less:
        files: ["resources/less/**/*"]
        tasks: ["lessBuild"]
      js:
        files: ["public/javascripts/**/*"]
        tasks: ["livereload"]
      css:
        files: ["public/stylesheets/**/*"]
        tasks: ["livereload"]
      app:
        files: ["app/**/*"]
        tasks: ["livereload"]
      conf:
        files: ["conf/*"]
        tasks: ["livereload"]
