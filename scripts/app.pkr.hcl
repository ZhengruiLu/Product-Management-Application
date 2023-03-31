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

variable "shared_account_id" {
  type    = string
  default = "859583877906"
}

locals {
  app_name = "ProductManager"
}

# https://www.packer.io/plugins/builders/amazon/ebs
source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  ami_regions = [
    "${var.aws_region}",
  ]

  ami_users = [
    "${var.shared_account_id}" //demo
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
    ]

    inline = [
      "sudo yum update -y",
      "yes | sudo yum install java-1.8.0-openjdk",
      "curl -O https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm",
      "sudo rpm -U ./amazon-cloudwatch-agent.rpm",
      "sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c ssm:/CloudWatchAgentConfig.json -s",
      "sudo yum clean all"
    ]
  }

  provisioner "file" {
    source      = "./ProductManager.jar"
    destination = "/tmp/ProductManager.jar"
  }

  provisioner "file" {
    source      = "./scripts/ProductManager.service"
    destination = "/tmp/ProductManager.service"
  }

  #systemd setup
  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ]

    inline = [
      "sudo mkdir /opt/app",
      "sudo mkdir /var/log/apps",
      "sudo mv /tmp/ProductManager.jar /opt/app/",
      "sudo mv /tmp/ProductManager.service /etc/systemd/system/",
      "sudo systemctl daemon-reload",
      "sudo systemctl enable ProductManager.service"
    ]
  }

  post-processor "manifest" {
    output     = "manifest.json"
    strip_path = true
  }
}


