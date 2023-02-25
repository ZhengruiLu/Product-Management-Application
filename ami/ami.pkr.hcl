variable "aws_region" {
  type    = string
  default = "us-west-1"
}

variable "source_ami" {
  type    = string
  default = "ami-00569e54da628d17c" # Amazon Linux 2
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}

variable "subnet_id" {
  type    = string
  default = "subnet-07f1c68d20abc9489"
}

# https://www.packer.io/plugins/builders/amazon/ebs
source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  ami_regions = [
    "us-west-1",
  ]

  aws_polling {
    delay_seconds = 120
    max_attempts  = 50
  }

  instance_type = "t2.micro"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"

  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 8
    volume_type           = "gp2"
  }
}

build {
  sources = ["source.amazon-ebs.my-ami"]

  provisioner "file" {
    source      = "./mysql_secure_installation.sh"
    destination = "/scripts/mysql_secure_installation.sh"
  }

  provisioner "file" {
    source      = "./ProductManager-0.0.1-SNAPSHOT.jar"
    destination = "/opt/ProductManager/ProductManager-0.0.1-SNAPSHOT.jar"
  }

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ]

    inline = [
      "sudo yum update -y",                        //pass
      "yes | sudo yum install java-1.8.0-openjdk", //add
      "sudo yum install -y mariadb-server",
      "sudo systemctl start mariadb",
      "sudo systemctl enable mariadb",
      #      "sudo mkdir /scripts && sudo chmod 777 /scripts",
      #      "sudo cp mysql_secure_installation.sh ~/scripts/",
      "sudo chmod +x /home/ec2-user/scripts/mysql_secure_installation.sh",
      "sudo bash /home/ec2-user/scripts/mysql_secure_installation.sh",
      "sudo mysql -u root -pChangChang@1 -e 'CREATE DATABASE usertestdb;'",
      "sudo yum clean all",
      #      "sudo mkdir -p /opt/ProductManager",
      #      "sudo cp ProductManager-0.0.1-SNAPSHOT.jar /opt/ProductManager/",
      "sudo chmod 755 /opt/ProductManager/",
      "sudo java -jar /opt/ProductManager/ProductManager-0.0.1-SNAPSHOT.jar",
    ]
  }
}


