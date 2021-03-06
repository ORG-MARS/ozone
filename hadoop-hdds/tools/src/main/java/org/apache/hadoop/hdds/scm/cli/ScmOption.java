/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm.cli;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hdds.HddsUtils;
import org.apache.hadoop.hdds.cli.GenericParentCommand;
import org.apache.hadoop.hdds.conf.MutableConfigurationSource;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.ScmConfigKeys;
import org.apache.hadoop.hdds.scm.client.ScmClient;
import picocli.CommandLine;

import java.io.IOException;

import static org.apache.hadoop.hdds.scm.ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY;
import static picocli.CommandLine.Spec.Target.MIXEE;

/**
 * Defines command-line option for SCM address.
 */
public class ScmOption {

  @CommandLine.Spec(MIXEE)
  private CommandLine.Model.CommandSpec spec;

  @CommandLine.Option(names = {"--scm"},
      description = "The destination scm (host:port)")
  private String scm;

  public ScmClient createScmClient() {
    try {
      GenericParentCommand parent = (GenericParentCommand)
          spec.root().userObject();
      OzoneConfiguration conf = parent.createOzoneConfiguration();
      checkAndSetSCMAddressArg(conf);

      return new ContainerOperationClient(conf);
    } catch (IOException ex) {
      throw new IllegalArgumentException("Can't create SCM client", ex);
    }
  }

  private void checkAndSetSCMAddressArg(MutableConfigurationSource conf) {
    if (StringUtils.isNotEmpty(scm)) {
      conf.set(OZONE_SCM_CLIENT_ADDRESS_KEY, scm);
    }
    if (!HddsUtils.getHostNameFromConfigKeys(conf,
        ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY).isPresent()) {

      throw new IllegalArgumentException(
          ScmConfigKeys.OZONE_SCM_CLIENT_ADDRESS_KEY
              + " should be set in ozone-site.xml or with the --scm option");
    }
  }

}
