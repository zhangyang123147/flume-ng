package org.apache.flume.node;

import org.apache.flume.core.Context;
import org.apache.flume.core.LogicalNode;
import org.apache.flume.lifecycle.LifecycleController;
import org.apache.flume.lifecycle.LifecycleException;
import org.apache.flume.lifecycle.LifecycleState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestFlumeNode {

  private FlumeNode node;

  @Before
  public void setUp() {
    node = new FlumeNode();

    node.setName("test-node");
    node.setNodeManager(new EmptyLogicalNodeManager());
  }

  @Test
  public void testLifecycle() throws InterruptedException, LifecycleException {
    Context context = new Context();

    node.start(context);
    boolean reached = LifecycleController.waitForOneOf(node,
        new LifecycleState[] { LifecycleState.START, LifecycleState.ERROR },
        5000);

    Assert.assertTrue("Matched a known state", reached);
    Assert.assertEquals(LifecycleState.START, node.getLifecycleState());

    node.stop(context);
    reached = LifecycleController.waitForOneOf(node, new LifecycleState[] {
        LifecycleState.STOP, LifecycleState.ERROR }, 5000);

    Assert.assertTrue("Matched a known state", reached);
    Assert.assertEquals(LifecycleState.STOP, node.getLifecycleState());
  }

  @Test
  public void testAddNodes() throws InterruptedException, LifecycleException {
    Context context = new Context();

    node.start(context);
    boolean reached = LifecycleController.waitForOneOf(node,
        new LifecycleState[] { LifecycleState.START, LifecycleState.ERROR },
        5000);

    Assert.assertTrue("Matched a known state", reached);
    Assert.assertEquals(LifecycleState.START, node.getLifecycleState());

    LogicalNode n1 = new LogicalNode();

    node.getNodeManager().add(n1);

    node.stop(context);
    reached = LifecycleController.waitForOneOf(node, new LifecycleState[] {
        LifecycleState.STOP, LifecycleState.ERROR }, 5000);

    Assert.assertTrue("Matched a known state", reached);
    Assert.assertEquals(LifecycleState.STOP, node.getLifecycleState());
  }

  public static class EmptyLogicalNodeManager extends
      AbstractLogicalNodeManager {

    private LifecycleState lifecycleState;

    @Override
    public void start(Context context) throws LifecycleException {
      lifecycleState = LifecycleState.START;
    }

    @Override
    public void stop(Context context) throws LifecycleException {
      lifecycleState = LifecycleState.STOP;
    }

    @Override
    public LifecycleState getLifecycleState() {
      return lifecycleState;
    }

  }

}
