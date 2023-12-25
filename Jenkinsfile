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
        AWS_ACCESSKEY="${AWS_ACCESSKEY}"
        AWS_SECRETKEY="${AWS_SECRETKEY}"
        AWS_BUCKETNAME="${AWS_BUCKETNAME}"
        AWS_REGION="${AWS_REGION}"
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
            --env AWS_ACCESSKEY=$AWS_ACCESSKEY \
            --env AWS_SECRETKEY=$AWS_SECRETKEY \
            --env AWS_BUCKETNAME=$AWS_BUCKETNAME \
            --env AWS_REGION=$AWS_REGION \
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