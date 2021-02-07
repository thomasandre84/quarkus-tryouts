#!groovy

pipeline {
	agent any
	parameters {
        booleanParam(name: 'docker', description: 'Build Docker Image', defaultValue: 'false')
		booleanParam(name: 'sonar', description: 'Scan Sonar', defaultValue: 'false')
		booleanParam(name: 'native', description: 'Build native images', defaultValue: 'false')
    }
	environment {
		GRPC = 'quarkus-grpc'
		REACTIVE_REST = 'quarkus-reactive-rest'
		JMS = 'quarkus-jms'
	}
	stages {
		stage("Build") {
			steps {
				sh "mvn clean install"
			}

			/*post {
				always {
					junit "target/surefire-reports/*.xml"
				}
				success {
					notify("Successful", params.email)
					notifySlack("Build Successful", "good")
				}
				failure {
					notify("Failure", params.email)
					notifySlack("Build Failure", "danger")
				}
			}*/
		}
		stage('Build native') {
			when {
				expression { params.native }
			}
			steps {
				sh "mvn clean package -Pnative"
			}
		}

		stage('SonarQube analysis') {
			when {
				expression { params.sonar }
			}
			steps {
				script {
					scannerHome = tool 'SonarQube Scanner';
				}
				withSonarQubeEnv('sonarQube') {
					sh "${scannerHome}/bin/sonar-scanner " +
							"-Dsonar.projectKey=$GRPC " +
							"-Dsonar.sources=${GRPC}/src " +
							"-Dsonar.java.source=11 " +
							"-Dsonar.java.binaries=${GRPC}/target/classes"

					sh "${scannerHome}/bin/sonar-scanner " +
							"-Dsonar.projectKey=$REACTIVE_REST " +
							"-Dsonar.sources=${REACTIVE_REST}/src " +
							"-Dsonar.java.source=11 " +
							"-Dsonar.java.binaries=${REACTIVE_REST}/target/classes"

					sh "${scannerHome}/bin/sonar-scanner " +
							"-Dsonar.projectKey=$JMS " +
							"-Dsonar.sources=${JMS}/src " +
							"-Dsonar.java.source=11 " +
							"-Dsonar.java.binaries=${JMS}/target/classes"
				}
			}
			/*post {
				failure {
					notify("SonarQube Failure", params.email)
					notifySlack("SonarQube Failure", "danger")
				}
			}*/
		}

		/*stage("Quality Gate") {
			options {
				timeout(time: 5, unit: 'MINUTES')
				retry(2)
			}
			steps {
				waitForQualityGate abortPipeline: true
			}
			post {
				failure {
					notify("Quality Gate Failure", params.email)
					notifySlack("Quality Gate Failure", "danger")
				}
			}
		}

		stage("Start App") {
			options {
				timeout(time: 2, unit: 'MINUTES')
				retry(2)
			}
			steps {
				sh "./start.sh"
				waitUntil {
					script {
						def r = sh script: 'wget -q http://localhost:8082/api/v1/locations -O /dev/null', returnStatus: true
						return (r == 0);
					}
				}
			}
			post {
				failure {
					notify("Start Application Failure", params.email)
					notifySlack("Start Application Failure", "danger")
					sh "kill `cat server.pid`"
					sh "rm server.pid"
				}
			}
		}

		stage("Test JMeter") {
			steps {
				echo "Executing JMeter Tests"
				//sh "${JENKINS_HOME}/tools/apache-jmeter/bin/jmeter \
				//	 -Jjmeter.save.saveservice.output_format=xml -n -t Test.jmx -l Test.jtl"
			}
			post {
				failure {
					notify("Test Failure", params.email)
					notifySlack("Test Failure", "danger")
				}
			}
		}

		stage("Test Performance"){
			steps {
				echo "Executing Performance Tests"
			}
			post {
				failure {
					notify("Performance Test Failure", params.email)
					notifySlack("Performance Test Failure", "danger")
				}
			}
		}

		stage('Start ZAP Proxy') {
			steps {
				script {
					startZap(host: "127.0.0.1", port: 8090, timeout:500, zapHome: "${JENKINS_HOME}/tools/ZAP", allowedHosts:['localhost'])//, sessionPath:"${WORKSPACE}/zap/locations.session")
				}
			}
			post {
				failure {
					notify("Start ZAP Proxy Failure", params.email)
					notifySlack("Start ZAP Proxy Failure", "danger")
				}
			}
		}
		stage("ZAP Attack") {
			steps {
				script {
					importZapUrls(path: "${WORKSPACE}/zap/beerindex_locations.txt") // or use a session
					// runZapCrawler(host: "http://localhost:8082/") // not required, since we do attacks on the urls
					importZapScanPolicy(policyPath: "${WORKSPACE}/zap/zap_scan.policy")
					runZapAttack(scanPolicyName: "Scan_Policy")
				}

			}
			post {
				always {
					script {
						archiveZap(failAllAlerts: 150, failHighAlerts: 1, failMediumAlerts: 0, failLowAlerts: 0, falsePositivesFilePath: "zapFalsePositives.json")
					}
				}
				failure {
					notify("ArchiveZap Failure", params.email)
					notifySlack("ArchiveZap Failure", "danger")
				}
			}
		}
		stage("Stop App"){
			steps {
				sh "kill `cat server.pid`"
				sh "rm server.pid"
			}
			post {
				failure {
					notify("Stop Application Failure", params.email)
					notifySlack("Stop Application Failure", "danger")
				}
			}
		}*/
		stage("Build and Push Docker Image"){
			when {
				expression { params.docker }
			}
			steps {
				sh "mvn docker:build"
			}

			/*post {
				failure {
					notify("Stop Application Failure", params.email)
					//notifySlack("Stop Application Failure", "danger")
				}
			}*/
		}

	}
}


def notify(result, email) {
	emailext (
		subject: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
		body: "Result: ${result}",
		recipientProviders: [[$class: 'DevelopersRecipientProvider']],
		to: email
	)
}

