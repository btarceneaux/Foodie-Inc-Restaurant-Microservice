pipeline {
    agent any 

    triggers {
        pollSCM('* * * * *')
    }
    // Got permission denied while trying to connect to the Docker daemon socket at unix.
    // sudo usermod -a -G docker jenkins
    // restart jenkins server ->  sudo service jenkins restart

    environment
    {
        DBUN="${DBUN}"
        DBPW="${DBPW}"
        AWS-ACCESSKEY="${AWS-ACCESSKEY}"
        AWS-SECRETKEY="${AWS-SECRETKEY}"
        AWS-BUCKETNAME="${AWS-BUCKETNAME}"
        AWS-REGION="${AWS-REGION}"
    }

    stages {
            
        stage('Maven Compile') {
            steps {
                echo '----------------- Compiling project ----------'
                sh 'mvn clean compile'
            }
        }
        
        stage('Maven Build') {
             steps {
                echo '----------------- Building project ----------'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                echo '----------------- Building docker image ----------'
                sh '''
                    docker image build -t restaurant-service .
                '''
            }
        }

        stage('Docker Deploy') {
            steps {
                echo '----------------- Deploying docker image ----------'
                sh '''
                 (if  [ $(docker ps -a | grep restaurant-service | cut -d " " -f1) ]; then \
                        echo $(docker rm -f restaurant-service); \
                        echo "---------------- successfully removed restaurant-service ----------------"
                     else \
                    echo OK; \
                 fi;);
            docker container run \
            --env DBUN=$DBUN \
            --env DBPW=$DBPW \
            --env AWS-ACCESSKEY=$AWS-ACCESSKEY \
            --env AWS-SECRETKEY=$AWS-SECRETKEY \
            --env AWS-BUCKETNAME=$AWS-BUCKETNAME \
            --env AWS-REGION=$AWS-REGION \
            --restart always \
            --name restaurant-service \
            -p 8082:8082 \
            -d restaurant-service && \
            docker network connect foodie-inc-network restaurant-service
            '''
            }
        }
    }
}