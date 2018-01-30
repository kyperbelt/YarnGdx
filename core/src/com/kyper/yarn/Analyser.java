package com.kyper.yarn;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.kyper.yarn.Analyser.Diagnosis.Severity;
import com.kyper.yarn.Program.Instruction;

public class Analyser {

	public static class Diagnosis {

		public enum Severity {
			Error, Warning, Note
		}

		public String message;
		public String node_name;
		public int line_number;
		public int col_number;

		public Severity severity;

		public Diagnosis(String message, Severity severity, String node_name, int line_number, int col_number) {
			this.message = message;
			this.severity = severity;
			this.node_name = node_name;
			this.line_number = line_number;
			this.col_number = col_number;
		}

		public Diagnosis(String message, Severity severity, String node_name, int line_number) {
			this(message, severity, node_name, line_number, -1);
		}

		public Diagnosis(String message, Severity severity, String node_name) {
			this(message, severity, node_name, -1);
		}

		public Diagnosis(String message, Severity severity) {
			this(message, severity, null);
		}

		public String toString(boolean show_severity) {
			String context_label = "";
			if (show_severity) {
				switch (severity) {
				case Error:
					context_label = "ERROR: ";
					break;
				case Warning:
					context_label = "WARNING: ";
					break;
				case Note:
					context_label = "Note: ";
					break;
				default:
					throw new IllegalArgumentException();
				}
			}

			if (node_name != null) {
				context_label += node_name;

				if (line_number != -1) {
					context_label += String.format(": %s", line_number);

					if (col_number != -1) {
						context_label += String.format(":%s", col_number);
					}
				}
			}

			String message;

			if (context_label == null || context_label.isEmpty()) {
				message = this.message;
			} else {
				message = String.format(" %1$s: %2$s", context_label, this.message);
			}

			return message;

		}
	}

	public static class Context {
		
		private Array<CompiledProgramAnalyser> analysers;
		public Context() {
			analysers = new Array<Analyser.CompiledProgramAnalyser>();
			analysers.add(new VariableLister());
			analysers.add(new UnusedVariableChecker());
		}
		
		public Context(CompiledProgramAnalyser...analysers) {
			this.analysers = new Array<Analyser.CompiledProgramAnalyser>(analysers);
		}
		
		
		protected void addProgramToAnalysis(Program program) {
			for (CompiledProgramAnalyser a : analysers) {
				a.diagnose(program);
			}
		}
		
		public Iterable<Diagnosis> finalAnalysis(){
			Array<Diagnosis> diagnoses = new Array<Analyser.Diagnosis>();
			for (CompiledProgramAnalyser a : analysers) {
				diagnoses.addAll(a.gatherDiagnoses());
			}
			return diagnoses;
		}
		
		

	}

	protected static abstract class ASTAnalyser {
		public abstract Iterable<Diagnosis> diagnose(Parser.Node node);
	}

	protected static abstract class CompiledProgramAnalyser {
		public abstract void diagnose(Program program);

		public abstract Array<Diagnosis> gatherDiagnoses();
	}

	protected static class VariableLister extends CompiledProgramAnalyser {

		protected ObjectSet<String> variables = new ObjectSet<String>();

		@Override
		public void diagnose(Program program) {
			//each node, find all reads and writes to variables
			for (Entry<String, Program.Node> nodeinfo : program.nodes) {

				Program.Node the_node = nodeinfo.value;

				for (Program.Instruction instruction : the_node.instructions) {
					switch (instruction.getOperation()) {
					case PushVariable:
					case StoreVariable:
						variables.add((String) instruction.operandA());
					default:
						break;
					}
				}
			}
		}

		@Override
		public Array<Diagnosis> gatherDiagnoses() {
			Array<Diagnosis> diagnoses = new Array<Analyser.Diagnosis>();

			for (String var : variables) {
				Diagnosis d = new Diagnosis("Script uses variable " + var, Severity.Note);
				diagnoses.add(d);
			}
			return diagnoses;
		}

	}

	protected static class UnusedVariableChecker extends CompiledProgramAnalyser {
		private ObjectSet<String> read_vars = new ObjectSet<String>();
		private ObjectSet<String> written_vars = new ObjectSet<String>();

		@Override
		public void diagnose(Program program) {
			//in each node, find all reads and writes to variables
			for (Entry<String, Program.Node> nodeinfo : program.nodes) {
				
				
				Program.Node node = nodeinfo.value;
				Array<Instruction> instructions = node.instructions;
				for (int i = 0; i < instructions.size; i++) {
					Instruction instruction = instructions.get(i);

					switch (instruction.getOperation()) {
					case PushVariable:
						read_vars.add((String) instruction.operandA());
						break;
					case StoreVariable:
						written_vars.add((String) instruction.operandA());
						break;
					default:
						break;
					}
				}
			}
		}

		@Override
		public Array<Diagnosis> gatherDiagnoses() {
			
			//exclude read variables that are also written
			Array<String> read_only = new Array<String>();
			read_only.addAll(read_vars.iterator().toArray());
			read_only.removeAll(written_vars.iterator().toArray(), false);
			
			//exclude write vars that are read
			Array<String> write_only = new Array<String>();
			write_only.addAll(written_vars.iterator().toArray());
			write_only.removeAll(read_vars.iterator().toArray(), false);
			
			//generate diagnoses
			Array<Diagnosis> diagnoses = new Array<Analyser.Diagnosis>();
			
			for (String ro : read_only) {
				String message = String.format("Variable %s is read from, but nevver assigned", ro);
				diagnoses.add(new Diagnosis(message, Severity.Warning));
			}
			
			for (String wo : write_only) {
				String message = String.format("Variable %s is assigned, but never read from", wo);
				diagnoses.add(new Diagnosis(message, Severity.Warning));
			}
			
			return diagnoses;
		}

	}

}
