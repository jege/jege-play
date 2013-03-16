module.exports = (grunt) ->
  grunt.loadNpmTasks("grunt-contrib-clean")
  grunt.loadNpmTasks("grunt-contrib-concat")
  grunt.loadNpmTasks("grunt-contrib-jshint")
  grunt.loadNpmTasks("grunt-contrib-uglify")
  grunt.loadNpmTasks("grunt-contrib-coffee")
  grunt.loadNpmTasks("grunt-contrib-livereload")
  grunt.loadNpmTasks("grunt-regarde")
  grunt.loadNpmTasks("grunt-recess")

  grunt.registerTask("default", ["build", "livereload-start", "regarde"])
  grunt.registerTask("build", ["coffeeCompile", "lessCompile"])
  grunt.registerTask("coffeeCompile", ["clean:coffee", "clean:js", "concat:coffee", "coffee", "uglify"])
  grunt.registerTask("lessCompile", ["clean:css", "recess"])

  grunt.initConfig
    pkg : grunt.file.readJSON('package.json')
    appdir: 'app'
    publicdir: 'public'
    resourcesdir: 'resources'
    jsdir: '<%= publicdir %>/javascripts'
    jsdirapp: '<%= jsdir %>/app'
    cssdir: '<%= publicdir %>/stylesheets'
    lessdir: '<%= resourcesdir %>/less'
    coffeedir: '<%= resourcesdir %>/coffee'
    gendir: '<%= resourcesdir %>/generated'
    gendircoffee: '<%= gendir %>/coffee'
    filename: '<%= pkg.name %>'
    filenameversion: '<%= filename %>-<%= pkg.version %>'

    meta:
      banner : '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
          '<%= grunt.template.today("yyyy-mm-dd") %>\n' + '<%= pkg.homepage ? "* " + pkg.homepage + "\n" : "" %>' +
          '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
          ' Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %> */'

    clean:
      coffee: ["<%= gendircoffee %>"]
      css: ["<%= cssdir %>"]
      js: ["<%= jsdirapp %>"]
      
    coffee:
      compile:
        files:
          '<%= jsdirapp %>/<%= filenameversion %>.js': '<%= gendircoffee %>/<%= filename %>.coffee'

    recess:
      raw:
        options:
          compile:true
        files:
          '<%= cssdir %>/<%= filenameversion %>.css': ['<%= lessdir %>/main.less']
      min:
        options:
          compile:true
          compress: true
        files:
          '<%= cssdir %>/<%= filenameversion %>.min.css': ['<%= cssdir %>/<%= filenameversion %>.css']

    uglify:
      js:
        files:
          '<%= jsdirapp %>/<%= filenameversion %>.min.js': [ '<%= jsdirapp %>/<%= filenameversion %>.js' ]

    jshint:
      options:
        curly: true
        eqeqeq: true
        immed: true
        latedef: true
        newcap: true
        noarg: true
        sub: true
        undef: true
        boss: true
        eqnull: true
        browser: true
        globals:
          jQuery: true
          angular: true
          console: true
          Prism: true
          app: true
      all: ['Gruntfile.js', '<%= jsdir %>/**/*.js']

    concat:
      coffee:
        files:
          '<%= gendir %>/coffee/<%= filename %>.coffee': ['<%= coffeedir %>/app.coffee', '<%= coffeedir %>/**/*.coffee']

    regarde:
      gruntfile:
        files: ["Gruntfile.coffee"]
        tasks: ["build"]
      coffee:
        files: ["resources/coffee/**/*"]
        tasks: ["coffeeCompile"]
      less:
        files: ["resources/less/**/*"]
        tasks: ["lessCompile"]
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
