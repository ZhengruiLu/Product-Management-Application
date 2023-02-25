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

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ] //must change to Amazon Linux
    inline = [
      "sudo yum update -y",                        //pass
      "yes | sudo yum install java-1.8.0-openjdk", //add
      "sudo yum install -y mariadb-server",
      "sudo systemctl start mariadb",
      "sudo systemctl enable mariadb",
      "echo -e '\n\n\n\nY\npassword\npassword\nY\nY\nY\nY\n' | sudo mysql_secure_installation",
      "sudo yum clean all"
    ] //must change to Amazon Linux
  }
}

#"sudo amazon-linux-extras install nginx1", //change to sudo amazon-linux-extras install nginx1
#"sudo systemctl start nginx",              //pass
#"sudo systemctl enable nginx",


#  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
#  ami_description = "AMI for CSYE 6225"

