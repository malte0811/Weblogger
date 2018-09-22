package malte0811.weblogger;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class WebLoggerTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		ClassReader reader = new ClassReader(basicClass);
		ClassNode clazz = new ClassNode();
		reader.accept(clazz, 0);
		for(MethodNode method : clazz.methods)
		{
			Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
			while(iterator.hasNext())
			{
				AbstractInsnNode node = iterator.next();
				if(node.getOpcode()==Opcodes.INVOKEVIRTUAL)
				{
					MethodInsnNode invoke = (MethodInsnNode)node;
					if(invoke.owner.equals("java/net/URL")&&invoke.name.startsWith("open"))
					{
						if(invoke.desc.startsWith("()"))
						{
							InsnList insert = new InsnList();
							addLoggingToList(insert);
							method.instructions.insertBefore(invoke, insert);
						} else if (invoke.name.equals("openConnection") && invoke.desc.equals("(Ljava/net/Proxy;)Ljava/net/URLConnection;")) {
							InsnList insert = new InsnList();
							insert.add(new InsnNode(Opcodes.SWAP));
							addLoggingToList(insert);
							insert.add(new InsnNode(Opcodes.SWAP));
							method.instructions.insertBefore(invoke, insert);
						}
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(0);//ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES) causes some weird issues
		clazz.accept(writer);
		return writer.toByteArray();
	}

	private void addLoggingToList(InsnList insert) {
		insert.add(new InsnNode(Opcodes.DUP));
		insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "malte0811/weblogger/WebLoggerHooks",
				"log", "(Ljava/net/URL;)V",
				false));
	}
}
